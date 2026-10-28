package com.blessed.blsd_api_bend.dto.servico;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ServicoRequestDTO {

    @NotBlank
    @Size(max = 100)
    private String nome;

    @Size(max = 255)
    @JsonAlias("descricacao")
    private String descricao;

    @NotNull
    @Positive
    private BigDecimal preco;

    @NotNull
    @Positive
    private Integer duracao;

}
