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
public class Empresa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @Schema(description = "Nome fantasia da empresa", example = "Tech Solutions LTDA")
    @Column(name = "nome_fantasia")
    private String nome;

    @Schema(description = "CNPJ da empresa", example = "12.345.678/0001-99")
    private String cnpj;

    @Schema(description = "E-mail de contato da empresa", example = "contato@techsolutions.com")
    private String email;

    @Schema(description = "Telefone de contato da empresa", example = "(11) 4002-8922")
    private String telefone;

    @OneToOne
    @JoinColumn(name = "endereco_id")
    private Endereco endereco;

}
