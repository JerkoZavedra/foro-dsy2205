package cl.duoc.foro.security;

/**
 * Principal de Spring Security para este microservicio: identidad resuelta
 * por ms-usuarios (nunca contrasenas ni hashes). Los controladores la leen
 * desde Authentication.getPrincipal() para fijar el autor real de un tema o
 * comentario, en vez de aceptar un autor enviado en el JSON.
 */
public record UsuarioAutenticado(Long id, String username, String rol) {}
