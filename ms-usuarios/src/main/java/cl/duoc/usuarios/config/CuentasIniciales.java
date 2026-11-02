package cl.duoc.usuarios.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import cl.duoc.usuarios.dto.UsuarioCrearRequest;
import cl.duoc.usuarios.model.Rol;
import cl.duoc.usuarios.repository.UsuarioRepository;
import cl.duoc.usuarios.service.UsuarioService;

/**
 * Carga educativa: crea tres cuentas (1 ADMIN + 2 USUARIO) SOLO la primera
 * vez que arranca con la tabla USUARIOS vacia. Las contrasenas se reciben
 * por variables de entorno y nunca se escriben en el codigo ni en los logs;
 * si faltan, el arranque continua pero registra una advertencia explicita
 * en el log (no queda incompleto de forma silenciosa).
 */
@Component
public class CuentasIniciales implements CommandLineRunner {
    private static final Logger log = LoggerFactory.getLogger(CuentasIniciales.class);

    private final UsuarioRepository repo;
    private final UsuarioService servicio;
    private final String admin;
    private final String usuario1;
    private final String usuario2;

    public CuentasIniciales(UsuarioRepository repo, UsuarioService servicio,
                             @Value("${foro.seed.admin:}") String admin,
                             @Value("${foro.seed.usuario1:}") String usuario1,
                             @Value("${foro.seed.usuario2:}") String usuario2) {
        this.repo = repo;
        this.servicio = servicio;
        this.admin = admin;
        this.usuario1 = usuario1;
        this.usuario2 = usuario2;
    }

    @Override
    public void run(String... args) {
        if (repo.count() > 0) {
            log.info("Ya existen cuentas en USUARIOS: no se ejecuta la carga inicial");
            return;
        }
        if (admin.length() < 8 || usuario1.length() < 8 || usuario2.length() < 8) {
            log.warn("ATENCION: no se crearon cuentas iniciales. Definir las variables de entorno "
                + "FORO_ADMIN_PASS, FORO_USUARIO1_PASS y FORO_USUARIO2_PASS (8+ caracteres) y "
                + "reiniciar ms-usuarios. Revisar el README para el detalle.");
            return;
        }
        servicio.crear(new UsuarioCrearRequest("admin", "admin@foro.test", admin, Rol.ADMIN));
        servicio.crear(new UsuarioCrearRequest("usuario1", "usuario1@foro.test", usuario1, Rol.USUARIO));
        servicio.crear(new UsuarioCrearRequest("usuario2", "usuario2@foro.test", usuario2, Rol.USUARIO));
        log.info("Tres cuentas de demostracion creadas (admin, usuario1, usuario2) con contrasenas hasheadas");
    }
}
