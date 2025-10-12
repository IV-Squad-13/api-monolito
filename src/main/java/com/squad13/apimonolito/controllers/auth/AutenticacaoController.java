package com.squad13.apimonolito.controllers.auth;

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
@RequestMapping("api/auth")
public class AutenticacaoController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private AutenticacaoService authService;

    @Autowired
    private TokenService tokenService;

    @GetMapping("/me")
    public ResponseEntity<Usuario> me(@RequestHeader("Authorization") String authHeader){
        return ResponseEntity.ok(authService.findMe(authHeader));
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginRequestDTO data) {
        if (data.email() == null && data.username() == null) return ResponseEntity.badRequest().build();
        var identifier = data.username() == null ? data.email() : data.username();

        var usernamePassword = new UsernamePasswordAuthenticationToken(identifier, data.password());

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

    @PutMapping("/{id}")
    public ResponseEntity<Object> updateUsuario(@PathVariable Long id, @Valid @RequestBody RegisterDto dto) {
        try {
            Usuario usuarioAtualizado = authService.updateUser(id, dto);
            return ResponseEntity.ok(usuarioAtualizado.getNome());
        }  catch (UsernameNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deletarUsuario(@PathVariable Long id) {
        try {
            authService.deleteUser(id);
            return ResponseEntity.noContent().build();
        } catch (UsernameNotFoundException e) {
            return ResponseEntity.notFound().build();
        }

    }
}
