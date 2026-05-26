package com.blessed.blsd_api_bend.dto.consulta;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AvaliacaoDTO {
    private Long id;
    private Integer nota;
    private String descricacao;
}