package com.blessed.blsd_api_bend.dto.dashboard;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Schema(description = "Receita total agrupada por ano")
public class ReceitaAnualDTO {

    @Schema(description = "Ano de referência", example = "2025")
    private Integer ano;

    @Schema(description = "Total faturado no ano", example = "38640.00")
    private BigDecimal total;

}