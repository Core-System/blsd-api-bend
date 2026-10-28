package com.blessed.blsd_api_bend.dto.consulta;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AvaliacaoRequestDTO {
    @NotNull
    @Min(1)
    @Max(5)
    private Integer nota;

    @Size(max = 255)
    private String descricao;
}