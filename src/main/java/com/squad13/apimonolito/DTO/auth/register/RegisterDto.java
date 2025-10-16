package com.squad13.apimonolito.DTO.auth.register;

import com.squad13.apimonolito.util.enums.PapelEnum;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterDto {

    @NotBlank(message = "Nome é obrigatório")
    @Size(max = 100, message = "Nome deve ter no máximo 100 caracteres")
    private String nome;

    @NotBlank(message = "Senha é obrigatória")
    @Size(min = 6, max = 255, message = "Senha deve ter entre 6 e 255 caracteres")
    private String senha;

    @Email(message = "Email deve ser válido")
    private String email;

    @NotNull(message = "Papel é obrigatório")
    private PapelEnum papel;
}