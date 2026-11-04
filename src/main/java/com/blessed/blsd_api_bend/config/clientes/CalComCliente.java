package com.blessed.blsd_api_bend.config.clientes;

import com.blessed.blsd_api_bend.dto.agendamento.*;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "calcomClient", url = "https://api.cal.com/v2")
public interface CalComCliente {

    @PostMapping("/bookings")
    ResponseEntity<CalComResponseDTO> criarAgendamento(
            @RequestHeader("Authorization") String authorization,
            @RequestHeader("cal-api-version") String apiVersion,
            @RequestBody CalComRequisicaoDTO request
    );

    @GetMapping("/bookings/{uid}")
    ResponseEntity<CalComReservaDetalheDTO> buscarReservaPorUid(
            @RequestHeader("Authorization") String authorization,
            @RequestHeader("cal-api-version") String apiVersion,
            @PathVariable("uid") String uid
    );

    @PostMapping("/bookings/{uid}/cancel")
    ResponseEntity<Void> cancelarReserva(
            @RequestHeader("Authorization") String authorization,
            @RequestHeader("cal-api-version") String apiVersion,
            @PathVariable("uid") String uid,
            @RequestBody CalComCancelamentoDTO request
    );

    @GetMapping("/slots")
    ResponseEntity<CalComSlotsResponseDTO> buscarSlots(
            @RequestHeader("Authorization") String authorization,
            @RequestHeader("cal-api-version") String apiVersion,
            @RequestParam("eventTypeId") Integer eventTypeId,
            @RequestParam("start") String start,
            @RequestParam("end") String end,
            @RequestParam("timeZone") String timeZone,
            @RequestParam("format") String format
    );
}
