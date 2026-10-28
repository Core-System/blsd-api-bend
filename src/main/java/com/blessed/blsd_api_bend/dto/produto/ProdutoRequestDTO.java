package com.blessed.blsd_api_bend.dto.produto;

import com.blessed.blsd_api_bend.model.entity.Servico;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.ManyToMany;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProdutoRequestDTO {


    private String nome;
    private BigDecimal preco;
    private Integer quantidade;
}
