package cl.duoc.usuarios.security;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import cl.duoc.usuarios.repository.UsuarioRepository;

/** Busca la cuenta en Oracle y entrega su hash BCrypt a Spring Security. */
@Service
public class UsuarioDetailsService implements UserDetailsService {
    private final UsuarioRepository repo;

    public UsuarioDetailsService(UsuarioRepository repo) {
        this.repo = repo;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        var usuario = repo.findByUsername(username).orElseThrow(() ->
            new UsernameNotFoundException("Usuario no encontrado"));
        return User.withUsername(usuario.getUsername())
            .password(usuario.getPassHash())
            .roles(usuario.getRol().name())
            .build();
    }
}
