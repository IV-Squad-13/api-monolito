package com.squad13.apimonolito.DTO.catalog;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Null;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ItemDTO {

    @Null
    private Long id;

    @NotBlank(message = "O nome do item é obrigatório.")
    @Size(max = 80, message = "O nome do item não pode ultrapassar 80 caracteres.")
    private String name;

    private Boolean isActive = true;

    private Long typeId;

    private String type;

    @NotBlank(message = "A descrição do item é obrigatória.")
    @Size(max = 320, message = "A descrição do item não pode ultrapassar 320 caracteres.")
    private String desc;
}
