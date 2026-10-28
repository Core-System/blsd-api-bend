package com.blessed.blsd_api_bend.controller;

import com.blessed.blsd_api_bend.dto.agendamento.AgendamentoRequestDTO;
import com.blessed.blsd_api_bend.dto.agendamento.ReservaDataDTO;
import com.blessed.blsd_api_bend.dto.consulta.*;
import com.blessed.blsd_api_bend.service.ConsultaService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import com.blessed.blsd_api_bend.model.enums.StatusConsulta;
import java.time.LocalDate;
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

    @GetMapping
    @PreAuthorize("hasAnyAuthority('GESTOR', 'FUNCIONARIO')")
    public ResponseEntity<Page<ConsultaResponseDTO>> listar(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "15") int size,
            @RequestParam(defaultValue = "asc") String sort,
            @RequestParam(required = false) StatusConsulta status,
            @RequestParam(required = false) String busca,
            @RequestParam(required = false) String procedimento,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fim) {
        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by(Sort.Direction.fromString(sort), "dataHoraInicio")
        );
        return ResponseEntity.ok(consultaService.listarTodos(
                status,
                busca,
                procedimento,
                inicio == null ? null : inicio.atStartOfDay(),
                fim == null ? null : fim.plusDays(1).atStartOfDay(),
                pageable
        ));
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