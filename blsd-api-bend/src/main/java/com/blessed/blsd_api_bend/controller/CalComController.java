package com.blessed.blsd_api_bend.controller;

import com.blessed.blsd_api_bend.dto.agendamento.AgendamentoRequestDTO;
import com.blessed.blsd_api_bend.service.CalComService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/api/calendario")
public class CalComController {
    private final CalComService calendarService;

    public CalComController(CalComService calendarService) {
        this.calendarService = calendarService;
    }

    @PostMapping("/calcom/agendar")
    public ResponseEntity<String> agendar(@RequestBody AgendamentoRequestDTO request){
        String resultado = calendarService.criarAgendamento(request.getNome(), request.getEmail(), request.getDataHoraInicio(), request.getProcedimento());

        return ResponseEntity.ok(resultado);
    }
}
