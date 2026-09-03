package com.blessed.blsd_api_bend.dto.funcionario;

import com.blessed.blsd_api_bend.model.entity.Acesso;
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
    private Acesso acesso;
    private String urlFoto;
}