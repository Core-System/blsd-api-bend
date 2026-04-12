package com.blessed.blsd_api_bend.dto.cliente;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ClienteRequestDTO {

    @NotBlank
    private String nome;
    @NotBlank
    private String email;
    @NotBlank
    private String senha;
    @NotBlank
    private String urlFoto;
    @NotNull
    @Past(message = "A data de nascimento deve ser anterior à data atual")
    private LocalDate dataNasc;
    @NotBlank
    private String telefone;
    @NotNull
    private Long acessoId;
    @NotNull
    private Long enderecoId;

    private Long consultaId;



}
