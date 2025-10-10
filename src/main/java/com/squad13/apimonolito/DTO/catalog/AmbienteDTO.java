package com.squad13.apimonolito.DTO.catalog;

import com.squad13.apimonolito.util.enums.LocalEnum;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class AmbienteDTO {

    @NotBlank(message = "O nome do ambiente é obrigatório.")
    @Size(max = 80, message = "O nome do ambiente não pode ultrapassar 80 caracteres.")
    private String name;

    @NotNull(message = "O ambiente deve estar associado a um local.")
    private LocalEnum local;

    private Boolean isActive = true;
}