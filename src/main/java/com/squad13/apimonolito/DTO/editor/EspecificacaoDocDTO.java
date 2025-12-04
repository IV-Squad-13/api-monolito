package com.squad13.apimonolito.DTO.editor;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.squad13.apimonolito.util.enums.DocInitializationEnum;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.bson.types.ObjectId;

import java.util.List;

public record EspecificacaoDocDTO(
        @NotBlank(message = "A Especificação precisa ter um nome")
        String name,

        String desc,
        String local,
        List<String> obs,

        @NotNull(message = "A Especificação precisa ter o id do Empreendimento")
        Long empId,

        Long empImportId,

        @JsonSerialize(using = ToStringSerializer.class)
        ObjectId docImportId,

        @NotNull(message = "Informe o tipo de inicialização para a Especificação")
        DocInitializationEnum initType
) { }