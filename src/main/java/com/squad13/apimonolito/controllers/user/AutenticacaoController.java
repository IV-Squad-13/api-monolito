package com.squad13.apimonolito.controllers.user;

import com.squad13.apimonolito.DTO.auth.login.LoginRequestDTO;
import com.squad13.apimonolito.DTO.auth.register.RegisterDto;
import com.squad13.apimonolito.DTO.auth.res.ResUserDTO;
import com.squad13.apimonolito.models.user.Usuario;
import com.squad13.apimonolito.services.user.AutenticacaoService;
import com.squad13.apimonolito.services.user.EmailService;
import com.squad13.apimonolito.services.user.TokenService;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("api/user")
@RequiredArgsConstructor
public class AutenticacaoController {

    private final AuthenticationManager authenticationManager;

    private final AutenticacaoService authService;

    private final TokenService tokenService;

    private final EmailService emailService;

    @GetMapping("/auth/me")
    public ResponseEntity<Usuario> me(@RequestHeader("Authorization") String authHeader) {
        return ResponseEntity.ok(authService.findMe(authHeader));
    }

    @PostMapping("/auth/login")
    public ResponseEntity<String> login(@RequestBody LoginRequestDTO data) {
        if (data.email() == null && data.username() == null) return ResponseEntity.badRequest().build();
        var identifier = data.username() == null ? data.email() : data.username();

        var usernamePassword = new UsernamePasswordAuthenticationToken(identifier, data.password());

        var auth = this.authenticationManager.authenticate(usernamePassword);

        var token = tokenService.generateToken((Usuario) auth.getPrincipal());

        return ResponseEntity.ok(token);
    }

    @PostMapping("/auth/register")
    public ResponseEntity<ResUserDTO> registrarUsuario(@Valid @RequestBody RegisterDto dto) throws IOException, MessagingException {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(authService.registerUser(dto));
    }
}
