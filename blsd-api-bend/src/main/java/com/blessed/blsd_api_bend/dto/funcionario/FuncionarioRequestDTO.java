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


    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public Empresa getEmpresa() {
        return empresa;
    }

    public void setEmpresa(Empresa empresa) {
        this.empresa = empresa;
    }
}
