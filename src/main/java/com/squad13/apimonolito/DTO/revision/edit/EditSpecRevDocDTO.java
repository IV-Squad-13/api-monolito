package com.squad13.apimonolito.DTO.revision.edit;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EditSpecRevDocDTO extends EditRevDocDTO {

    private Boolean isNameApproved;
    private Boolean isDescApproved;
    private Boolean isObsApproved;
}