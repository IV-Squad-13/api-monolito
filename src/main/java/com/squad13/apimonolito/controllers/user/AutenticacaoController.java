package com.squad13.apimonolito.controllers.user;

import com.squad13.apimonolito.DTO.auth.login.LoginRequestDTO;
import com.squad13.apimonolito.DTO.auth.register.RegisterDto;
import com.squad13.apimonolito.models.user.Usuario;
import com.squad13.apimonolito.services.user.AutenticacaoService;
import com.squad13.apimonolito.services.user.TokenService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("api/user")
public class AutenticacaoController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private AutenticacaoService authService;

    @Autowired
    private TokenService tokenService;

    @GetMapping("/auth/me")
    public ResponseEntity<Usuario> me(@RequestHeader("Authorization") String authHeader){
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
