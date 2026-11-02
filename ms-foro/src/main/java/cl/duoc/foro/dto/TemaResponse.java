package cl.duoc.foro.dto;

import java.time.Instant;
import cl.duoc.foro.model.Tema;

public record TemaResponse(Long id, Long categoriaId, Long autorId, String autorUsername,
                            String titulo, String contenido, Instant fechaCreacion) {
    public static TemaResponse de(Tema t) {
        return new TemaResponse(t.getId(), t.getCategoriaId(), t.getAutorId(), t.getAutorUsername(),
            t.getTitulo(), t.getContenido(), t.getFechaCreacion());
    }
}
