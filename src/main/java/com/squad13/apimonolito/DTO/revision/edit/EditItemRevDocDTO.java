package com.squad13.apimonolito.DTO.revision.edit;

import com.squad13.apimonolito.models.revision.structures.RevDocElement;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EditItemRevDocDTO extends EditRevDocDTO {

    private Boolean isDescApproved;
    private Boolean isTypeApproved;
}