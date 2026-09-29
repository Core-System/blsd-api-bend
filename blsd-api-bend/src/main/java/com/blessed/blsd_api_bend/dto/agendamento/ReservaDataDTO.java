package com.blessed.blsd_api_bend.dto.agendamento;

import java.util.List;

public record ReservaDataDTO(
        Long id,
        String uid,
        String title,
        String description,
        String start,
        String end,
        String status,
        String cancellationReason,
        List<AttendeeDTO> attendees
) {}