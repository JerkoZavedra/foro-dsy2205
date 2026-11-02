package cl.duoc.usuarios.service;

import java.util.List;
import java.util.Locale;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import cl.duoc.usuarios.dto.UsuarioActualizarRequest;
import cl.duoc.usuarios.dto.UsuarioCrearRequest;
import cl.duoc.usuarios.dto.UsuarioResponse;
import cl.duoc.usuarios.error.NegocioException;
import cl.duoc.usuarios.model.Rol;
import cl.duoc.usuarios.model.Usuario;
import cl.duoc.usuarios.repository.UsuarioRepository;

/** Reglas de negocio de cuentas: el Controller nunca administra contrasenas ni hashes. */
@Service
public class UsuarioService {
    private static final Logger log = LoggerFactory.getLogger(UsuarioService.class);

    private final UsuarioRepository repo;
    private final PasswordEncoder encoder;

    public UsuarioService(UsuarioRepository repo, PasswordEncoder encoder) {
        this.repo = repo;
        this.encoder = encoder;
    }

    @Transactional(readOnly = true)
    public List<UsuarioResponse> listar() {
        return repo.findAll().stream().map(UsuarioResponse::de).toList();
    }

    @Transactional(readOnly = true)
    public UsuarioResponse buscar(Long id) {
        return UsuarioResponse.de(obtener(id));
    }

    private Usuario obtener(Long id) {
        return repo.findById(id).orElseThrow(() ->
            new NegocioException(HttpStatus.NOT_FOUND, "Usuario no encontrado: " + id));
    }

    @Transactional
    public UsuarioResponse crear(UsuarioCrearRequest req) {
        String username = req.username().toLowerCase(Locale.ROOT);
        String email = req.email().toLowerCase(Locale.ROOT);
        if (repo.existsByUsername(username) || repo.existsByEmail(email)) {
            throw new NegocioException(HttpStatus.CONFLICT, "Username o correo ya registrado");
        }
        Usuario creado = repo.save(new Usuario(null, username, email,
            encoder.encode(req.password()), req.rol()));
        log.info("Cuenta creada id={} rol={}", creado.getId(), creado.getRol());
        return UsuarioResponse.de(creado);
    }

    @Transactional
    public UsuarioResponse actualizar(Long id, UsuarioActualizarRequest req) {
        Usuario actual = obtener(id);
        String email = req.email().toLowerCase(Locale.ROOT);
        if (!actual.getEmail().equals(email) && repo.existsByEmail(email)) {
            throw new NegocioException(HttpStatus.CONFLICT, "Correo ya registrado");
        }
        if (actual.getRol() == Rol.ADMIN && req.rol() != Rol.ADMIN && repo.countByRol(Rol.ADMIN) <= 1) {
            throw new NegocioException(HttpStatus.CONFLICT, "No se puede quitar el ultimo ADMIN");
        }
        actual.setEmail(email);
        actual.setRol(req.rol());
        // Una contrasena omitida (null) conserva la anterior; una nueva se vuelve a codificar.
        if (req.password() != null) {
            actual.setPassHash(encoder.encode(req.password()));
        }
        Usuario guardado = repo.save(actual);
        log.info("Cuenta actualizada id={}", id);
        return UsuarioResponse.de(guardado);
    }

    @Transactional
    public void eliminar(Long id) {
        Usuario actual = obtener(id);
        if (actual.getRol() == Rol.ADMIN && repo.countByRol(Rol.ADMIN) <= 1) {
            throw new NegocioException(HttpStatus.CONFLICT, "No se puede eliminar el ultimo ADMIN");
        }
        repo.delete(actual);
        log.info("Cuenta eliminada id={}", id);
    }
}
