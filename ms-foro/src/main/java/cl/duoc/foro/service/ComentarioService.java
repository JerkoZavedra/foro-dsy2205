package cl.duoc.foro.service;

import java.time.LocalDateTime;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import cl.duoc.foro.dto.ComentarioRequest;
import cl.duoc.foro.dto.ComentarioResponse;
import cl.duoc.foro.error.NegocioException;
import cl.duoc.foro.model.Comentario;
import cl.duoc.foro.repository.ComentarioRepository;
import cl.duoc.foro.security.UsuarioAutenticado;

/** Modificar o eliminar un comentario solo lo puede hacer su autor. */
@Service
public class ComentarioService {
    private static final Logger log = LoggerFactory.getLogger(ComentarioService.class);

    private final ComentarioRepository repo;
    private final TemaService temaService;

    public ComentarioService(ComentarioRepository repo, TemaService temaService) {
        this.repo = repo;
        this.temaService = temaService;
    }

    @Transactional(readOnly = true)
    public List<ComentarioResponse> listarPorTema(Long temaId) {
        temaService.obtener(temaId);
        return repo.findByTemaIdOrderByIdAsc(temaId).stream().map(ComentarioResponse::de).toList();
    }

    @Transactional(readOnly = true)
    public ComentarioResponse buscar(Long id) {
        return ComentarioResponse.de(obtener(id));
    }

    private Comentario obtener(Long id) {
        return repo.findById(id).orElseThrow(() ->
            new NegocioException(HttpStatus.NOT_FOUND, "Comentario no encontrado: " + id));
    }

    @Transactional
    public ComentarioResponse crear(Long temaId, ComentarioRequest datos, UsuarioAutenticado autor) {
        temaService.obtener(temaId);
        Comentario nuevo = new Comentario(null, temaId, autor.id(), autor.username(),
            datos.contenido(), LocalDateTime.now());
        Comentario guardado = repo.save(nuevo);
        log.info("Comentario creado id={} tema={} autor={}", guardado.getId(), temaId, autor.username());
        return ComentarioResponse.de(guardado);
    }

    @Transactional
    public ComentarioResponse actualizar(Long id, ComentarioRequest datos, UsuarioAutenticado autor) {
        Comentario actual = obtener(id);
        TemaService.exigirPropietario(actual.getAutorId(), autor);
        actual.setContenido(datos.contenido());
        Comentario guardado = repo.save(actual);
        log.info("Comentario actualizado id={}", id);
        return ComentarioResponse.de(guardado);
    }

    @Transactional
    public void eliminar(Long id, UsuarioAutenticado autor) {
        Comentario actual = obtener(id);
        TemaService.exigirPropietario(actual.getAutorId(), autor);
        repo.delete(actual);
        log.info("Comentario eliminado id={}", id);
    }
}
