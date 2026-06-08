package com.blessed.blsd_api_bend.dto.avaliacao;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AvaliacaoResponseDTO {
    private Long id;
    private Integer nota;
    private String descricacao;
    private String clienteNome;
    private String clienteUrlFoto;
    private List<String> servicos;
}