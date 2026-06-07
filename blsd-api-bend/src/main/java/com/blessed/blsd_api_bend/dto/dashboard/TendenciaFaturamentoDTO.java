package com.blessed.blsd_api_bend.dto.dashboard;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Schema(description = "Faturamento mensal agrupado por ano e mês")
public class TendenciaFaturamentoDTO {

    @Schema(description = "Ano de referência", example = "2025")
    private Integer ano;

    @Schema(description = "Mês de referência", example = "5")
    private Integer mes;

    @Schema(description = "Total faturado no período", example = "1130.00")
    private BigDecimal total;

}