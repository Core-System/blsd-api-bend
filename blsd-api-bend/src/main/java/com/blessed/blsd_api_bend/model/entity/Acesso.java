package com.blessed.blsd_api_bend.model.entity;

import com.blessed.blsd_api_bend.model.enums.TiposAcessos;
import io.swagger.v3.oas.annotations.media.Schema;
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


    @Schema(description = "Tipo de acesso do usuário",
            example = "ADMIN",
            implementation = TiposAcessos.class)
    @Enumerated(EnumType.STRING)
    @Column(name = "nome")
    private TiposAcessos nome;

    @Schema(description = "Descrição detalhada do acesso",
            example = "Acesso administrativo com permissões completas")
    private String descricao;
}
