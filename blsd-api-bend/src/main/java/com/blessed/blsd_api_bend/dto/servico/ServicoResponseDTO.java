package com.blessed.blsd_api_bend.dto.servico;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ServicoResponseDTO {

    private Long id;
    private String nome;
    private String descricao;
    private BigDecimal preco;
    private Integer duracao;
    private String categoria;
    private String imgUrl;
}
