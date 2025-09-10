package com.squad13.apimonolito.controllers;

import com.squad13.apimonolito.dto.req.TempUserReqDTO;
import com.squad13.apimonolito.dto.res.TempUserResDTO;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/users")
public class TempUsersController {

    @GetMapping
    public ResponseEntity<List<TempUserResDTO>> getAllUsers() {
        TempUserResDTO user0 = new TempUserResDTO(0L, "A", "A", "A", "A", "A");
        TempUserResDTO user1 = new TempUserResDTO(1L, "B", "B", "B", "B", "B");

        return ResponseEntity.ok().body(Arrays.asList(user0, user1));
    }

    @PostMapping("/login")
    public ResponseEntity<TempUserResDTO> login(@RequestBody @Valid TempUserReqDTO tempUserReqDTO) {
        return ResponseEntity.ok(new TempUserResDTO(
                0L,
                "admin",
                tempUserReqDTO.email(),
                tempUserReqDTO.password(),
                "admin",
                "admin-time"
        ));
    }
}