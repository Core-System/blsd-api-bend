package com.blessed.blsd_api_bend.dto.cliente;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class ClienteResponseDTO {
    private Long id;
    private String nome;
    private String email;
    private LocalDate dataNasc;
    private String telefone;
    private String urlFoto;
}
