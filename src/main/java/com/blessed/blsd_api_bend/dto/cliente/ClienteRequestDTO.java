package com.blessed.blsd_api_bend.dto.cliente;

import com.blessed.blsd_api_bend.dto.usuario.UsuarioRequestDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ClienteRequestDTO extends UsuarioRequestDTO {

    @Schema(description = "Data de nascimento do usuário (deve ser anterior à data atual)",
            example = "1990-05-20")
    @NotNull
    @Past(message = "A data de nascimento deve ser anterior à data atual")
    private LocalDate dataNasc;

    @Schema(description = "Telefone de contato do usuário",
            example = "(11) 91234-5678")
    @NotBlank
    private String telefone;




}
