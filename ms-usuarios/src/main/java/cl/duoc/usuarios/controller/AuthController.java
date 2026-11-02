package cl.duoc.usuarios.controller;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import cl.duoc.usuarios.dto.PerfilResponse;
import cl.duoc.usuarios.model.Usuario;
import cl.duoc.usuarios.repository.UsuarioRepository;

/**
 * El login HTTP Basic no genera token ni mantiene sesion: Spring Security
 * valida el usuario y la contrasena (hash BCrypt) en cada peticion a este
 * endpoint, y si las credenciales son correctas responde 200 con el perfil;
 * si son incorrectas, el filtro de seguridad responde 401 antes de llegar
 * aqui. ms-foro usa GET /api/auth/perfil con las mismas credenciales para
 * resolver el autor real de un tema o comentario.
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final UsuarioRepository repo;

    public AuthController(UsuarioRepository repo) {
        this.repo = repo;
    }

    @PostMapping("/login")
    public PerfilResponse login(Authentication autenticado) {
        return perfil(autenticado);
    }

    @GetMapping("/perfil")
    public PerfilResponse perfil(Authentication autenticado) {
        Usuario usuario = repo.findByUsername(autenticado.getName()).orElseThrow();
        return new PerfilResponse(usuario.getId(), usuario.getUsername(), usuario.getRol().name());
    }
}
