package com.blessed.blsd_api_bend.service;

import com.blessed.blsd_api_bend.exception.avaliacao.AvaliacaoNotFoundException;
import com.blessed.blsd_api_bend.model.entity.Avaliacao;
import com.blessed.blsd_api_bend.repository.AvaliacaoRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AvaliacaoService {

    private final AvaliacaoRepository avaliacaoRepository;

    public AvaliacaoService(AvaliacaoRepository avaliacaoRepository) {
        this.avaliacaoRepository = avaliacaoRepository;
    }

    public List<Avaliacao> listarTodos() {
        return avaliacaoRepository.findAll();
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