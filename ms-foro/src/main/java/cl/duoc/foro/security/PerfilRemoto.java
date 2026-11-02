package cl.duoc.foro.security;

/** Respuesta de GET /api/auth/perfil en ms-usuarios. */
public record PerfilRemoto(Long id, String username, String rol) {}
