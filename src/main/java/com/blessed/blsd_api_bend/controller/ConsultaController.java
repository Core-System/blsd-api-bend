package com.blessed.blsd_api_bend.controller;

import com.blessed.blsd_api_bend.dto.agendamento.AgendamentoRequestDTO;
import com.blessed.blsd_api_bend.dto.agendamento.ReservaDataDTO;
import com.blessed.blsd_api_bend.dto.consulta.*;
import com.blessed.blsd_api_bend.service.ConsultaService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/consulta")
public class ConsultaController {

    private final ConsultaService consultaService;

    public ConsultaController(ConsultaService consultaService) {
        this.consultaService = consultaService;
    }

    @PostMapping("/agendar")
    public ResponseEntity<ConsultaResponseDTO> agendar(@Valid @RequestBody AgendamentoRequestDTO request) {
        ConsultaResponseDTO novaConsulta = consultaService.criarAgendamento(request);
        return ResponseEntity.ok(novaConsulta);
    }

    @GetMapping("/cliente/{clienteId}")
    @PreAuthorize("hasAnyAuthority('GESTOR', 'FUNCIONARIO') or (hasAuthority('CLIENTE') and #clienteId == principal.id)")
    public ResponseEntity<List<ConsultaResponseDTO>> listarPorCliente(
            @PathVariable Long clienteId) {
        return ResponseEntity.ok(consultaService.listarPorCliente(clienteId));
    }

    @PostMapping("/{consultaId}/avaliar")
    @PreAuthorize("hasAnyAuthority('GESTOR', 'FUNCIONARIO', 'CLIENTE')")
    public ResponseEntity<ConsultaResponseDTO> avaliar(
            @PathVariable Long consultaId,
            @Valid @RequestBody AvaliacaoRequestDTO dto) {
        return ResponseEntity.ok(consultaService.avaliar(consultaId, dto));
    }

    @GetMapping("/{consultaId}/detalhes-externos")
    @PreAuthorize("hasAnyAuthority('GESTOR', 'FUNCIONARIO', 'CLIENTE')")
    public ResponseEntity<ReservaDataDTO> buscarDetalhesCalCom(@PathVariable Long consultaId) {
        return ResponseEntity.ok(consultaService.buscarDetalhesExternos(consultaId));
    }
}