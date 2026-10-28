package com.blessed.blsd_api_bend.dto.produto;

import com.blessed.blsd_api_bend.model.entity.MovimentacaoATT;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// MovimentacaoRequestDTO.java
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MovimentacaoRequestDTO {
    private Long produtoId;
    private MovimentacaoATT.TipoMovimentacao tipo;
    private Integer quantidade;
    private String observacao;
}

