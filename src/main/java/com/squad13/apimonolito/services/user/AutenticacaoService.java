package com.squad13.apimonolito.services.user;

import com.squad13.apimonolito.DTO.auth.register.RegisterDto;
import com.squad13.apimonolito.DTO.auth.res.ResUserDTO;
import com.squad13.apimonolito.exceptions.exceptions.InvalidTokenException;
import com.squad13.apimonolito.exceptions.exceptions.ResourceAlreadyExistsException;
import com.squad13.apimonolito.exceptions.exceptions.ResourceNotFoundException;
import com.squad13.apimonolito.models.user.Papel;
import com.squad13.apimonolito.models.user.Usuario;
import com.squad13.apimonolito.repository.user.PapelRepository;
import com.squad13.apimonolito.repository.user.UsuarioRepository;
import com.squad13.apimonolito.util.enums.PapelEnum;
import jakarta.mail.MessagingException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AutenticacaoService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    private final PapelRepository papelRepository;

    private final PasswordEncoder passwordEncoder;

    private final TokenService tokenService;

    private final EmailService emailService;

    public Usuario findMe(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new InvalidTokenException("Token inválido");
        }

        String token = authHeader.substring(7);
        String username = tokenService.getSubject(token);

        return usuarioRepository.findByNome(username)
                .orElseGet(() -> usuarioRepository.findByEmail(username)
                        .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado: " + username)));
    }

    @Override
    public UserDetails loadUserByUsername(String identifier) throws UsernameNotFoundException {
        Optional<Usuario> usuario = usuarioRepository.findByNome(identifier);

        if (usuario.isPresent()) {
            return usuario.get();
        } else {
            usuario = usuarioRepository.findByEmail(identifier);
            if (usuario.isPresent()) {
                return usuario.get();
            }

            throw new UsernameNotFoundException("Usuário não encontrado" + identifier);
        }
    }

    @Transactional
    public ResUserDTO registerUser(RegisterDto dto) throws IOException, MessagingException {
        if (usuarioRepository.existsByNome(dto.getNome())) {
            throw new ResourceAlreadyExistsException("Nome de usuário já existe");
        }

        if (dto.getEmail() != null && !dto.getEmail().isBlank()
                && usuarioRepository.existsByEmail(dto.getEmail())) {
            throw new ResourceAlreadyExistsException("Email já cadastrado");
        }

        Papel papel = papelRepository.findByNome(dto.getPapel())
                .orElseThrow(() -> new ResourceNotFoundException("Papel não encontrado no sistema"));

        Usuario usuario = new Usuario();
        usuario.setNome(dto.getNome());
        usuario.setSenha(passwordEncoder.encode(dto.getSenha()));
        usuario.setEmail(dto.getEmail());
        usuario.setPapel(papel);

        usuario = usuarioRepository.save(usuario);

        if (usuario.getEmail() != null) {
            String template = emailService.loadTemplate("created-user/created-user.html");
            Map<String, String> vars = Map.of(
                    "nome", usuario.getNome(),
                    "username", usuario.getUsername(),
                    "email", usuario.getEmail(),
                    "senha", dto.getSenha()
            );

            String filledTemplate = emailService.applyVariables(template, vars);
            emailService.sendHtmlMail(
                    usuario.getEmail(),
                    "Conta criada com sucesso",
                    filledTemplate
            );
        }

        return ResUserDTO.from(usuario);
    }
}

