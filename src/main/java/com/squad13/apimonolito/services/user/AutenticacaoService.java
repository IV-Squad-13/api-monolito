package com.squad13.apimonolito.services.user;

import com.squad13.apimonolito.DTO.register.RegisterDto;
import com.squad13.apimonolito.exceptions.InvalidTokenException;
import com.squad13.apimonolito.models.user.Papel;
import com.squad13.apimonolito.models.user.Usuario;
import com.squad13.apimonolito.repository.user.PapelRepository;
import com.squad13.apimonolito.repository.user.UsuarioRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AutenticacaoService implements UserDetailsService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PapelRepository papelRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private final TokenService tokenService;

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
    public Usuario registerUser(RegisterDto dto) {

        List<String> papeisValidos = Arrays.asList("ADMIN", "REVISOR", "RELATOR");
        String papelInformado = dto.getPapel().toUpperCase().trim();

        if (!papeisValidos.contains(papelInformado)) {
            throw new IllegalArgumentException("Papel inválido. Deve ser ADMIN, REVISOR ou RELATOR");
        }

        if (usuarioRepository.existsByNome(dto.getNome())) {
            throw new IllegalArgumentException("Nome de usuário já existe");
        }

        if (dto.getEmail() != null && !dto.getEmail().isEmpty() && usuarioRepository.existsByEmail(dto.getEmail())) {
            throw new IllegalArgumentException("Email já cadastrado");
        }

        // Valida o Papel
        Papel papel = papelRepository.findByNome(papelInformado)
                .orElseThrow(() -> new IllegalArgumentException("Papel não encontrado no sistema"));

        Usuario usuario = new Usuario();
        usuario.setNome(dto.getNome());
        usuario.setSenha(passwordEncoder.encode(dto.getSenha()));
        usuario.setEmail(dto.getEmail());
        usuario.setPapel(papel);

        return usuarioRepository.save(usuario);
    }

    @Transactional
    public Usuario updateUser(Long id, RegisterDto dto) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário com id " + id + " não encontrado."));

        List<String> papeisValidos = Arrays.asList("ADMIN", "REVISOR", "RELATOR");


        if (dto.getNome() != null && !dto.getNome().isEmpty() && !dto.getNome().equals(usuario.getNome())) {
            if (usuarioRepository.existsByNome(dto.getNome())) {
                throw new IllegalArgumentException("Nome de usuário " + dto.getNome() + " já existe.");
            }
            usuario.setNome(dto.getNome());
        }

        if (dto.getEmail() != null && !dto.getEmail().isEmpty() && !usuario.getEmail().isEmpty()) {
            if (usuarioRepository.existsByEmail(dto.getEmail())) {
                throw new IllegalArgumentException("Email " + dto.getEmail() + " já cadastrado.");
            }
            usuario.setEmail(dto.getEmail());
        }

        if (dto.getSenha() != null && !dto.getSenha().isEmpty()) {
            usuario.setSenha(passwordEncoder.encode(dto.getSenha()));
        }

        if (dto.getPapel() != null && !dto.getPapel().isEmpty()) {
            String papelInformado = dto.getPapel().toUpperCase().trim();

            if (!papeisValidos.contains(papelInformado)) {
                throw new IllegalArgumentException("Papél inválido. Deve ser ADMIN, REVISOR ou RELATOR");
            }

            if (!papelInformado.equals(usuario.getPapel().getNome())) {
                Papel papel = papelRepository.findByNome(papelInformado)
                        .orElseThrow(() -> new IllegalArgumentException("Papel não encontrado no sistema."));
                usuario.setPapel(papel);
            }
        }

        return usuarioRepository.save(usuario);


    }

    @Transactional
    public void deleteUser(Long id){
        if (!usuarioRepository.existsById(id)) {
            throw new UsernameNotFoundException("Usuário com id " + id + " não encontrado.");
        }
        usuarioRepository.deleteById(id);
    }

}

