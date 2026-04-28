package com.blessed.blsd_api_bend.dto.cliente;

import com.blessed.blsd_api_bend.dto.usuario.UsuarioRequestDTO;
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


    @NotNull
    @Past(message = "A data de nascimento deve ser anterior à data atual")
    private LocalDate dataNasc;
    @NotBlank
    private String telefone;




}
