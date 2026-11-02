package cl.duoc.usuarios.dto;

import cl.duoc.usuarios.model.Rol;
import cl.duoc.usuarios.model.Usuario;

/** Nunca incluye password ni passHash. */
public record UsuarioResponse(Long id, String username, String email, Rol rol) {
    public static UsuarioResponse de(Usuario usuario) {
        return new UsuarioResponse(usuario.getId(), usuario.getUsername(),
            usuario.getEmail(), usuario.getRol());
    }
}
