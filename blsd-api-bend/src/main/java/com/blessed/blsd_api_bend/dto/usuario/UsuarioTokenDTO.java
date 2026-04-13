package com.blessed.blsd_api_bend.dto.usuario;

import lombok.Data;

@Data
public class UsuarioTokenDTO {

    private Long id;
    private String nome;
    private String email;
    private String token;

}
