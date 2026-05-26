package com.blessed.blsd_api_bend.controller;

import com.blessed.blsd_api_bend.dto.consulta.*;
import com.blessed.blsd_api_bend.service.ConsultaService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/consulta")
public class ConsultaController {

    private final ConsultaService consultaService;

    public ConsultaController(ConsultaService consultaService) {
        this.consultaService = consultaService;
    }

    @GetMapping("/cliente/{clienteId}")
    public ResponseEntity<List<ConsultaResponseDTO>> listarPorCliente(
            @PathVariable Long clienteId) {
        return ResponseEntity.ok(consultaService.listarPorCliente(clienteId));
    }

    @PostMapping("/{consultaId}/avaliar")
    public ResponseEntity<ConsultaResponseDTO> avaliar(
            @PathVariable Long consultaId,
            @RequestBody AvaliacaoRequestDTO dto) {
        return ResponseEntity.ok(consultaService.avaliar(consultaId, dto));
    }
}