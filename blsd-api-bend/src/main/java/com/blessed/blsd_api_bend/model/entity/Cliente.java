package com.blessed.blsd_api_bend.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Cliente extends Usuario{

    @Column(name = "data_nasc")
    private LocalDate dataNasc;

    private String telefone;

    @OneToOne
    @JoinColumn(name = "endereco_id")
    private Endereco endereco;

    @OneToMany(mappedBy = "cliente")
    private List<Consulta> consulta;


}
