package com.squad13.apimonolito.services.user;

import com.squad13.apimonolito.models.user.Usuario;
import com.squad13.apimonolito.repository.user.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AutenticacaoService implements UserDetailsService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<Usuario> usuario = usuarioRepository.findByNome(username);

        if (usuario.isPresent()) {
            return usuario.get();
        } else {
            throw new UsernameNotFoundException("Usuário não encontrado" + username);
        }
    }
}
