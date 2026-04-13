package com.blessed.blsd_api_bend.dto.cliente;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDate;

public class ClienteResponseDTO {
    private String nome;
    private String email;
    private String senha;
    private LocalDate dataNasc;
    private String telefone;
}
