package com.blessed.blsd_api_bend.dto.usuario;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class UsuarioListarDTO {

    private Long id;

    private String nome;

    private String email;

}
