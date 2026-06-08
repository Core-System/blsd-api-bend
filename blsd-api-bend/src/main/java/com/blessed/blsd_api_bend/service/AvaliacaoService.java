package com.blessed.blsd_api_bend.service;

import com.blessed.blsd_api_bend.dto.avaliacao.AvaliacaoResponseDTO;
import com.blessed.blsd_api_bend.exception.avaliacao.AvaliacaoNotFoundException;
import com.blessed.blsd_api_bend.model.entity.Avaliacao;
import com.blessed.blsd_api_bend.model.entity.Consulta;
import com.blessed.blsd_api_bend.model.entity.Servico;
import com.blessed.blsd_api_bend.repository.AvaliacaoRepository;
import com.blessed.blsd_api_bend.repository.ConsultaRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AvaliacaoService {

    private final AvaliacaoRepository avaliacaoRepository;
    private final ConsultaRepository consultaRepository;

    public AvaliacaoService(AvaliacaoRepository avaliacaoRepository, ConsultaRepository consultaRepository) {
        this.avaliacaoRepository = avaliacaoRepository;
        this.consultaRepository = consultaRepository;
    }

    public List<AvaliacaoResponseDTO> listarTodos() {
        return consultaRepository.findConsultasAvaliadas()
                .stream()
                .map(this::toDTO)
                .toList();
    }

    private AvaliacaoResponseDTO toDTO(Consulta c) {
        Avaliacao av = c.getAvaliacao();
        return new AvaliacaoResponseDTO(
                av.getId(),
                av.getNota(),
                av.getDescricao(),
                c.getCliente() != null ? c.getCliente().getNome() : null,
                c.getCliente() != null ? c.getCliente().getUrlFoto() : null,
                c.getServicos() != null
                        ? c.getServicos().stream().map(Servico::getNome).toList()
                        : List.of()
        );
    }

    public Avaliacao listarPorId(Long id) {
        return avaliacaoRepository.findById(id)
                .orElseThrow(() -> new AvaliacaoNotFoundException("Avaliação não encontrada"));
    }

    public Avaliacao cadastrar(Avaliacao avaliacao) {
        return avaliacaoRepository.save(avaliacao);
    }

    public Avaliacao atualizar(Long id, Avaliacao avaliacao) {
        return avaliacaoRepository.findById(id).map(a -> {
            a.setNota(avaliacao.getNota());
            a.setDescricao(avaliacao.getDescricao());
            return avaliacaoRepository.save(a);
        }).orElseThrow(() -> new AvaliacaoNotFoundException("Avaliação não encontrada"));
    }

    public void deletar(Long id) {
        Avaliacao avaliacao = listarPorId(id);
        avaliacaoRepository.delete(avaliacao);
    }
}