package com.blessed.blsd_api_bend.dto.agendamento;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public record CalComSlotsResponseDTO(
        String status,
        Map<String, List<SlotDTO>> data
) {
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record SlotDTO(
            String start,
            String end
    ) {}
}
