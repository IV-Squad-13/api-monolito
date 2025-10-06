package com.squad13.apimonolito.controllers.auth;

import com.squad13.apimonolito.DTO.login.LoginRequestDTO;
import com.squad13.apimonolito.DTO.register.RegisterDto;
import com.squad13.apimonolito.models.user.Usuario;
import com.squad13.apimonolito.services.user.AutenticacaoService;
import com.squad13.apimonolito.services.user.TokenService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("api/auth")
public class AutenticacaoController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private AutenticacaoService authService;

    @Autowired
    private TokenService tokenService;

    @PostMapping("/login")
    public ResponseEntity<Object> login(@RequestBody LoginRequestDTO data) {
        var usernamePassword = new UsernamePasswordAuthenticationToken(data.username(), data.password());

        var auth = this.authenticationManager.authenticate(usernamePassword);

        var token = tokenService.generateToken((Usuario) auth.getPrincipal());

        return ResponseEntity.ok(token);
    }

    @PostMapping("/register")
    public ResponseEntity<Object> registrarUsuario(@Valid @RequestBody RegisterDto dto) {
        try {
            Usuario usuario = authService.registerUser(dto);

            Map<String, Object> response = new HashMap<>();
            response.put("mensagem", "Usuário registrado com sucesso");
            response.put("id", usuario.getId());
            response.put("nome", usuario.getNome());
            response.put("papel", usuario.getPapel().getNome());

            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (IllegalArgumentException e) {
            Map<String, String> error = new HashMap<>();
            error.put("erro", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
}
