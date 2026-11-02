package cl.duoc.foro.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/** El autor NUNCA viaja en este DTO: se obtiene desde la identidad autenticada. */
public record TemaRequest(
    @NotNull Long categoriaId,
    @NotBlank @Size(max = 150) String titulo,
    @NotBlank @Size(max = 4000) String contenido
) {}
