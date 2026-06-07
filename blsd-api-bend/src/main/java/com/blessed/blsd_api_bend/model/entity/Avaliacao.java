package com.blessed.blsd_api_bend.model.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Avaliacao {

    @Id
    @GeneratedValue
    private Long id;

    private Integer nota;

    private String descricao;

}
