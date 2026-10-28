package com.blessed.blsd_api_bend.dto.produto;

import com.blessed.blsd_api_bend.model.entity.MovimentacaoATT;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

// MovimentacaoResponseDTO.java
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MovimentacaoResponseDTO {
    private Long id;
    private Long produtoId;
    private String nomeProduto;
    private MovimentacaoATT.TipoMovimentacao tipo;
    private Integer quantidade;
    private LocalDateTime dataHora;
    private String observacao;
}