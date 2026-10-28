package com.blessed.blsd_api_bend.controller;

import com.blessed.blsd_api_bend.dto.produto.MovimentacaoRequestDTO;
import com.blessed.blsd_api_bend.dto.produto.MovimentacaoResponseDTO;
import com.blessed.blsd_api_bend.model.entity.MovimentacaoATT;
import com.blessed.blsd_api_bend.service.MovimentacaoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/movimentacao")
public class MovimentacaoController {

    private final MovimentacaoService movimentacaoService;

    public MovimentacaoController(MovimentacaoService movimentacaoService) {
        this.movimentacaoService = movimentacaoService;
    }

    @GetMapping
    public ResponseEntity<List<MovimentacaoResponseDTO>> listarUltimas() {
        return ResponseEntity.ok(
                movimentacaoService.listarUltimas().stream()
                        .map(m -> new MovimentacaoResponseDTO(
                                m.getId(),
                                m.getProduto().getId(),
                                m.getProduto().getNome(),
                                m.getTipo(),
                                m.getQuantidade(),
                                m.getDataHora(),
                                m.getObservacao()
                        )).toList()
        );
    }

    @PostMapping
    public ResponseEntity<MovimentacaoResponseDTO> registrar(
            @RequestBody MovimentacaoRequestDTO dto) {
        MovimentacaoATT mov = movimentacaoService.registrar(dto);
        return ResponseEntity.status(201).body(new MovimentacaoResponseDTO(
                mov.getId(),
                mov.getProduto().getId(),
                mov.getProduto().getNome(),
                mov.getTipo(),
                mov.getQuantidade(),
                mov.getDataHora(),
                mov.getObservacao()
        ));
    }
}