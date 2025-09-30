package com.squad13.apimonolito.DTO.user;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;

@Data
@AllArgsConstructor
public class TempUserDTO {
    private Long id;
    private String name;
    private String email;
    private String password;
    private String role;
    private String created;
}