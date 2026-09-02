package com.blessed.blsd_api_bend.dto.funcionario;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@NoArgsConstructor
@AllArgsConstructor
@Data
public class FuncionarioResponseDTO {
    private Long id;
    private String nome;
    private String email;
    private String cpf;
    private String urlFoto;
}