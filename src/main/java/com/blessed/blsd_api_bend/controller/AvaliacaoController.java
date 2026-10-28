package com.blessed.blsd_api_bend.controller;

import com.blessed.blsd_api_bend.dto.avaliacao.AvaliacaoResponseDTO;
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
    public ResponseEntity<List<AvaliacaoResponseDTO>> listarAvaliacoes() {
        return ResponseEntity.ok(avaliacaoService.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Avaliacao> listarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(avaliacaoService.listarPorId(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarAvaliacao(@PathVariable Long id) {
        avaliacaoService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}