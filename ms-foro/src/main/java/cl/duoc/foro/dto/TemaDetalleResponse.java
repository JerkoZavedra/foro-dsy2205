package cl.duoc.foro.dto;

import java.time.Instant;
import java.util.List;
import cl.duoc.foro.model.Tema;

/** Detalle de un tema: incluye sus comentarios para evitar una segunda llamada. */
public record TemaDetalleResponse(Long id, Long categoriaId, Long autorId, String autorUsername,
                                   String titulo, String contenido, Instant fechaCreacion,
                                   List<ComentarioResponse> comentarios) {
    public static TemaDetalleResponse de(Tema t, List<ComentarioResponse> comentarios) {
        return new TemaDetalleResponse(t.getId(), t.getCategoriaId(), t.getAutorId(), t.getAutorUsername(),
            t.getTitulo(), t.getContenido(), t.getFechaCreacion(), comentarios);
    }
}
