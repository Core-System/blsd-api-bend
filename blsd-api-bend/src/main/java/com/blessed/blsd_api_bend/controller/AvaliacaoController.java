package com.blessed.blsd_api_bend.controller;

import com.blessed.blsd_api_bend.dto.avaliacao.AvaliacaoDTO;
import com.blessed.blsd_api_bend.model.entity.Avaliacao;
import com.blessed.blsd_api_bend.service.AvaliacaoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/avaliacao")
public class AvaliacaoController {

    private final AvaliacaoService avaliacaoService;

    public AvaliacaoController(AvaliacaoService avaliacaoService) {
        this.avaliacaoService = avaliacaoService;
    }

    @GetMapping
    public ResponseEntity<List<Avaliacao>> listarAvaliacoes() {
        return ResponseEntity.ok(avaliacaoService.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Avaliacao> listarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(avaliacaoService.listarPorId(id));
    }

    @PostMapping
    public ResponseEntity<Avaliacao> criarAvaliacao(@RequestBody AvaliacaoDTO dto) {
        Avaliacao avaliacao = new Avaliacao();
        avaliacao.setNota(dto.getNota());
        avaliacao.setDescricao(dto.getDescricao());
        return ResponseEntity.status(201).body(avaliacaoService.cadastrar(avaliacao));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Avaliacao> atualizarAvaliacao(@PathVariable Long id,
                                                        @RequestBody AvaliacaoDTO dto) {
        Avaliacao avaliacao = new Avaliacao();
        avaliacao.setNota(dto.getNota());
        avaliacao.setDescricao(dto.getDescricao());
        return ResponseEntity.ok(avaliacaoService.atualizar(id, avaliacao));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarAvaliacao(@PathVariable Long id) {
        avaliacaoService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}