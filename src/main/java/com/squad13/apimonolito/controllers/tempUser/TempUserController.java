package com.squad13.apimonolito.controllers.tempUser;

import com.squad13.apimonolito.DTO.user.TempUserDTO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
public class TempUserController {

    @PostMapping("/login")
    public TempUserDTO login() {
        return new TempUserDTO(
                1L,
                "admin",
                "admin@admin.com",
                "admin",
                "admin",
                "2025-08-02T10:00:00Z"
        );
    }
}