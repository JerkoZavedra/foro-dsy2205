package cl.duoc.usuarios.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import cl.duoc.usuarios.model.Rol;

/** Peticion PUT: el username no cambia; la contrasena es opcional (null = se conserva). */
public record UsuarioActualizarRequest(
    @NotBlank @Email @Size(max = 120) String email,

    @Size(min = 8, max = 72) String password,

    @NotNull Rol rol
) {}
