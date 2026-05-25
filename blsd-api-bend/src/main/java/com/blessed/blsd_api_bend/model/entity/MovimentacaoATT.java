package com.blessed.blsd_api_bend.model.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "movimentacao")
@Getter
@Setter
public class MovimentacaoATT {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "produto_id", nullable = false)
    private Produto produto;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoMovimentacao tipo;

    @Column(nullable = false)
    private Integer quantidade;

    @Column(nullable = false)
    private LocalDateTime dataHora = LocalDateTime.now();

    private String observacao;

    public enum TipoMovimentacao {
        ENTRADA, SAIDA
    }
}