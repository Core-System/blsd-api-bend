package com.blessed.blsd_api_bend.dto.usuario;

import com.blessed.blsd_api_bend.model.entity.Acesso;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class UsuarioTokenDTO {
    private String nome;
    private String token;
}
