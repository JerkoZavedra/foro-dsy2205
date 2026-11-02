package cl.duoc.usuarios.dto;

/**
 * Identidad del usuario autenticado. ms-foro consulta este contrato
 * (GET /api/auth/perfil con las mismas credenciales HTTP Basic que recibio)
 * para resolver el autor real de un tema o comentario, sin aceptar nunca
 * un autor arbitrario enviado en el JSON de la peticion.
 */
public record PerfilResponse(Long id, String username, String rol) {}
