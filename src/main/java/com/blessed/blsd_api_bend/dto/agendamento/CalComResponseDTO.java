package com.blessed.blsd_api_bend.dto.agendamento;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.OffsetDateTime;

@JsonIgnoreProperties(ignoreUnknown = true)
public record CalComResponseDTO(
        String status,
        CalBookingDataDTO data
) {
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record CalBookingDataDTO(
            Long id,
            String uid,
            String status,
            @JsonProperty("startTime")
            OffsetDateTime startTime,
            @JsonProperty("endTime")
            OffsetDateTime endTime,
            String title,
            String description
    ) {}
}
