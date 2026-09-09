package com.blessed.blsd_api_bend.config.clientes;

import com.blessed.blsd_api_bend.dto.agendamento.CalComRequisicaoDTO;
import com.blessed.blsd_api_bend.dto.agendamento.CalComResponseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "calcomClient", url = "https://api.cal.com/v2")
public interface CalComCliente {

    @PostMapping("/bookings")
    ResponseEntity<CalComResponseDTO> criarAgendamento(
            @RequestHeader("Authorization") String authorization,
            @RequestHeader("cal-api-version") String apiVersion,
            @RequestBody CalComRequisicaoDTO request
    );
}
