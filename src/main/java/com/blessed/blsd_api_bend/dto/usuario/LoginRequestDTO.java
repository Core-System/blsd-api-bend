package com.blessed.blsd_api_bend.dto.usuario;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class LoginRequestDTO {
    @Schema(description = "E-mail do usuário para login",
            example = "usuario@email.com")
    @NotBlank
    @Email
    private String email;

    @Schema(description = "Senha do usuário (mínimo 8 caracteres)",
            example = "senhaSegura123")
    @NotBlank
    @Size(min = 8, message = "A senha deve ter no mínimo 8 caracteres")
    private String senha;

}
