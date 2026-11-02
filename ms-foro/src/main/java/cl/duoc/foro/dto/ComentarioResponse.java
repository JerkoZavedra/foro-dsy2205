package cl.duoc.foro.dto;

import java.time.Instant;
import cl.duoc.foro.model.Comentario;

public record ComentarioResponse(Long id, Long temaId, Long autorId, String autorUsername,
                                  String contenido, Instant fechaCreacion) {
    public static ComentarioResponse de(Comentario c) {
        return new ComentarioResponse(c.getId(), c.getTemaId(), c.getAutorId(), c.getAutorUsername(),
            c.getContenido(), c.getFechaCreacion());
    }
}
