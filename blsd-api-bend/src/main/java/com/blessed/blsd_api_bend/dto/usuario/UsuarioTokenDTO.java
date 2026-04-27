package com.blessed.blsd_api_bend.dto.usuario;

import com.blessed.blsd_api_bend.model.entity.Acesso;
import lombok.Data;
import lombok.experimental.SuperBuilder;


@Data
public class UsuarioTokenDTO {

    private Long id;
    private String nome;
    private String email;
    private String token;
    private Acesso acesso;
}
