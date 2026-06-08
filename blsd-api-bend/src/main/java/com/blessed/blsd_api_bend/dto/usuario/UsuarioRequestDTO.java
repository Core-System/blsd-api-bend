package com.blessed.blsd_api_bend.dto.usuario;

import com.blessed.blsd_api_bend.model.entity.Acesso;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class UsuarioRequestDTO {
    @Schema(description = "Nome completo do usuário", example = "Maria Silva")
    @NotBlank
    private String nome;

    @Schema(description = "E-mail válido do usuário", example = "maria.silva@email.com")
    @NotBlank
    @Email
    private String email;

    @Schema(description = "Senha do usuário (mínimo 8 caracteres)", example = "senhaSegura123")
    @NotBlank
    @Size(min = 8)
    private String senha;

    @Schema(description = "URL da foto de perfil", example = "https://meuservidor.com/fotos/maria.jpg")
//    @NotBlank
    private String urlFoto;


    @NotNull
    private Acesso acesso;
}
