package com.blessed.blsd_api_bend.dto.consulta;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AvaliacaoRequestDTO {
    private Integer nota;
    private String descricao;
}