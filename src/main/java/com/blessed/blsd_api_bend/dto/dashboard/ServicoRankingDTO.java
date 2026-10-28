package com.blessed.blsd_api_bend.dto.dashboard;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Schema(description = "Ranking dos serviços mais realizados")
public class ServicoRankingDTO {

    @Schema(description = "Nome do serviço", example = "Limpeza de Pele")
    private String servico;

    @Schema(description = "Quantidade de vezes realizado", example = "42")
    private Long quantidade;

}