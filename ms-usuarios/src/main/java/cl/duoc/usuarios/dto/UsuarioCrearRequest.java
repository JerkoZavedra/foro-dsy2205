package cl.duoc.usuarios.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import cl.duoc.usuarios.model.Rol;

/** Peticion POST: solo un ADMIN puede crear cuentas; no existe registro publico. */
public record UsuarioCrearRequest(
    @NotBlank @Size(min = 3, max = 50)
    @Pattern(regexp = "[a-zA-Z0-9._-]+",
        message = "El username solo admite letras, numeros, punto, guion y guion bajo")
    String username,

    @NotBlank @Email @Size(max = 120) String email,

    @NotBlank @Size(min = 8, max = 72) String password,

    @NotNull Rol rol
) {}
