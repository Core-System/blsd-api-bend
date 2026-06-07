package com.blessed.blsd_api_bend.dto.dashboard;

import com.blessed.blsd_api_bend.model.enums.LocalConsulta;
import com.blessed.blsd_api_bend.model.enums.Pagamentos;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Schema(description = "Dados do próximo agendamento")
public class ProximoAgendamentoDTO {

    @Schema(description = "ID da consulta", example = "1")
    private Long consultaId;

    @Schema(description = "Data e hora de início", example = "2025-05-26T10:00:00")
    private LocalDateTime dataHoraInicio;

    @Schema(description = "Data e hora de término", example = "2025-05-26T11:00:00")
    private LocalDateTime dataHoraFim;

    @Schema(description = "Nome do cliente", example = "Ana Valéria")
    private String nomeCliente;

    @Schema(description = "URL da foto do cliente", example = "https://blessed.com.br/fotos/ana.jpg")
    private String urlFotoCliente;

    @Schema(description = "Local da consulta", example = "CLINICA")
    private LocalConsulta localConsulta;

    @Schema(description = "Tipo de pagamento", example = "PIX")
    private Pagamentos tipoPagamento;

}
