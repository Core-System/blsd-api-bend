package com.blessed.blsd_api_bend.dto.cliente;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class ClienteResponseDTO {
    private String nome;
    private String email;
    private String senha;
    private LocalDate dataNasc;
    private String telefone;
}
