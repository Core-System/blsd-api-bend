package com.blessed.blsd_api_bend.model.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Funcionario extends Usuario {

    @Column
    private String cpf;

    @OneToOne
    @JoinColumn(name = "empresa_id")
    private Empresa empresa;

    @OneToMany(mappedBy = "funcionario", cascade = CascadeType.ALL)
    private List<Consulta> consultas = new ArrayList<>();


}
