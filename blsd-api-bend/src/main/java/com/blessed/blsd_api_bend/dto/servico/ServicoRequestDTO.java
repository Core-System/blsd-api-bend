package com.blessed.blsd_api_bend.dto.servico;

import com.blessed.blsd_api_bend.model.entity.Avaliacao;
import com.blessed.blsd_api_bend.model.entity.Produto;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ServicoRequestDTO {

    private String nome;
    private String descricacao;
    private BigDecimal preco;
    private Integer duracao;

    private Avaliacao avaliacao;

    private List<Produto> produto;

}
