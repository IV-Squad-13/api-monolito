package com.squad13.apimonolito.DTO.editor;

import com.squad13.apimonolito.util.enums.DocElementEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.io.Serializable;

@Getter
@Setter
@ParameterObject
@AllArgsConstructor
public class DocElementParams {
    private DocElementEnum type;
}