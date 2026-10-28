package com.blessed.blsd_api_bend.dto.consulta;

import java.time.LocalDateTime;
import java.util.List;

public record ConsultaResponseDTO(
        Long id,
        LocalDateTime dataHoraInicio,
        LocalDateTime dataHoraFim,
        String statusConsulta,
        List<String> servicos,
        AvaliacaoDTO avaliacao,
        String calBookingUid
) {}