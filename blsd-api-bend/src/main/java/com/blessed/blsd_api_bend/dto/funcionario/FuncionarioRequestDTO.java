package com.blessed.blsd_api_bend.dto.funcionario;

import com.blessed.blsd_api_bend.dto.usuario.UsuarioRequestDTO;
import com.blessed.blsd_api_bend.model.entity.Empresa;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class FuncionarioRequestDTO extends UsuarioRequestDTO {

    private String cpf;

    private Empresa empresa;


}
