package com.blessed.blsd_api_bend.model.entity;

import com.blessed.blsd_api_bend.model.enums.TiposAcessos;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Acesso {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nome")
    private TiposAcessos nome;

    private String descricao;


}
