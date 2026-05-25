package com.blessed.blsd_api_bend.model.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Endereco {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Schema(description = "CEP do endereço", example = "01001-000")
    @Column(nullable = false)
    private String cep;

    @Schema(description = "Logradouro (rua, avenida, etc.)", example = "Praça da Sé")
    @Column(nullable = false)
    private String logradouro;

    @Schema(description = "Bairro do endereço", example = "Sé")
    @Column(nullable = false)
    private String bairro;

    @Schema(description = "Cidade do endereço", example = "São Paulo")
    @Column(nullable = false)
    private String cidade;

    @Schema(description = "Unidade Federativa (UF)", example = "SP")
    @Column(nullable = false)
    private String uf;

    @Schema(description = "Número do imóvel", example = "123")
    @Column(nullable = false)
    private String numero;

    @Schema(description = "Complemento do endereço", example = "Apartamento 45, Bloco B")
    private String complemento;


}

