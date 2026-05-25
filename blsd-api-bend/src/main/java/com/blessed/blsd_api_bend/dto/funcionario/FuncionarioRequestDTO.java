package com.blessed.blsd_api_bend.dto.funcionario;

import com.blessed.blsd_api_bend.dto.usuario.UsuarioRequestDTO;
import com.blessed.blsd_api_bend.model.entity.Empresa;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.br.CPF;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class FuncionarioRequestDTO extends UsuarioRequestDTO {

    @Schema(description = "CPF do usuário vinculado ao acesso",
            example = "123.456.789-00")
    @CPF
    private String cpf;

    private Empresa empresa;


}
