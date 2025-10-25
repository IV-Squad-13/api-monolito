package com.squad13.apimonolito.services.user;

import com.squad13.apimonolito.DTO.auth.register.RegisterDto;
import com.squad13.apimonolito.DTO.auth.res.ResUserDTO;
import com.squad13.apimonolito.models.user.Papel;
import com.squad13.apimonolito.models.user.Usuario;
import com.squad13.apimonolito.repository.user.PapelRepository;
import com.squad13.apimonolito.repository.user.UsuarioRepository;
import com.squad13.apimonolito.util.enums.PapelEnum;
import com.squad13.apimonolito.util.mapper.UserMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {

    private final UserMapper userMapper;

    private final PapelRepository papelRepository;
    private final UsuarioRepository usuarioRepository;

    private final PasswordEncoder passwordEncoder;

    public List<ResUserDTO> getAll() {
        return usuarioRepository.findAll()
                .stream().map(ResUserDTO::from)
                .toList();
    }

    private Usuario findById(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário com id " + id + " não encontrado."));
    }

    @Transactional
    public ResUserDTO updateUser(Long id, RegisterDto dto) {
        Usuario usuario = findById(id);

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

        if (dto.getPapel() != null) {
            PapelEnum papelInformado = dto.getPapel();

            if (!papelInformado.equals(usuario.getPapel().getNome())) {
                Papel papel = papelRepository.findByNome(papelInformado)
                        .orElseThrow(() -> new IllegalArgumentException("Papel não encontrado no sistema."));
                usuario.setPapel(papel);
            }
        }

        return ResUserDTO.from(usuarioRepository.save(usuario));
    }

    @Transactional
    public void deleteUser(Long id){
        if (!usuarioRepository.existsById(id)) {
            throw new UsernameNotFoundException("Usuário com id " + id + " não encontrado.");
        }
        usuarioRepository.deleteById(id);
    }

    // Buscar por Role

    public List<ResUserDTO> getUsersByPapel(PapelEnum papel) {
        return usuarioRepository.findByPapel_Nome(papel)
                .stream()
                .map(usuario -> new ResUserDTO(
                        usuario.getId(),
                        usuario.getNome(),
                        usuario.getEmail(),
                        usuario.getPapel().getNome()
                ))
                .collect(Collectors.toList());
    }


}