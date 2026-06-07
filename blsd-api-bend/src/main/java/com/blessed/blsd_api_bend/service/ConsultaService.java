package com.blessed.blsd_api_bend.service;

import com.blessed.blsd_api_bend.dto.consulta.*;
import com.blessed.blsd_api_bend.model.entity.*;
import com.blessed.blsd_api_bend.model.enums.StatusConsulta;
import com.blessed.blsd_api_bend.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class ConsultaService {

    private final ConsultaRepository consultaRepository;
    private final AvaliacaoRepository avaliacaoRepository;

    public ConsultaService(ConsultaRepository consultaRepository,
                           AvaliacaoRepository avaliacaoRepository) {
        this.consultaRepository = consultaRepository;
        this.avaliacaoRepository = avaliacaoRepository;
    }

    public List<ConsultaResponseDTO> listarPorCliente(Long clienteId) {
        return consultaRepository.findByClienteId(clienteId)
                .stream().map(this::toDTO).toList();
    }

    @Transactional
    public ConsultaResponseDTO avaliar(Long consultaId, AvaliacaoRequestDTO dto) {
        Consulta consulta = consultaRepository.findById(consultaId)
                .orElseThrow(() -> new RuntimeException("Consulta não encontrada"));

        if (consulta.getStatusConsulta() != StatusConsulta.CONCLUIDA) {
            throw new RuntimeException("Só é possível avaliar consultas concluídas");
        }

        Avaliacao avaliacao = new Avaliacao();
        avaliacao.setNota(dto.getNota());
        avaliacao.setDescricao(dto.getDescricacao());
        avaliacaoRepository.save(avaliacao);

        consulta.setAvaliacao(avaliacao);
        consultaRepository.save(consulta);

        return toDTO(consulta);
    }

    private ConsultaResponseDTO toDTO(Consulta c) {
        AvaliacaoDTO avaliacaoDTO = null;
        if (c.getAvaliacao() != null) {
            avaliacaoDTO = new AvaliacaoDTO(
                    c.getAvaliacao().getId(),
                    c.getAvaliacao().getNota(),
                    c.getAvaliacao().getDescricao()
            );
        }
        return new ConsultaResponseDTO(
                c.getId(),
                c.getDataHoraInicio(),
                c.getDataHoraFim(),
                c.getStatusConsulta() != null ? c.getStatusConsulta().name() : "PENDENTE",
                c.getServicos() != null
                        ? c.getServicos().stream().map(Servico::getNome).toList()
                        : List.of(),
                avaliacaoDTO
        );
    }
}