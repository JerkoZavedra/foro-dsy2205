package cl.duoc.foro.service;

import java.time.Instant;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import cl.duoc.foro.dto.ComentarioResponse;
import cl.duoc.foro.dto.TemaDetalleResponse;
import cl.duoc.foro.dto.TemaRequest;
import cl.duoc.foro.dto.TemaResponse;
import cl.duoc.foro.error.NegocioException;
import cl.duoc.foro.model.Tema;
import cl.duoc.foro.repository.ComentarioRepository;
import cl.duoc.foro.repository.TemaRepository;
import cl.duoc.foro.security.UsuarioAutenticado;

/**
 * Reglas de negocio de temas. Modificar o eliminar solo lo puede hacer el
 * autor (comparando autorId con la identidad autenticada); no existe
 * moderacion administrativa. Eliminar un tema elimina sus comentarios por
 * ON DELETE CASCADE en Oracle (ver sql/02_crear_tablas_foro.sql).
 */
@Service
public class TemaService {
    private static final Logger log = LoggerFactory.getLogger(TemaService.class);

    private final TemaRepository repo;
    private final ComentarioRepository comentarioRepo;
    private final CategoriaService categoriaService;

    public TemaService(TemaRepository repo, ComentarioRepository comentarioRepo,
                        CategoriaService categoriaService) {
        this.repo = repo;
        this.comentarioRepo = comentarioRepo;
        this.categoriaService = categoriaService;
    }

    @Transactional(readOnly = true)
    public List<TemaResponse> buscar(Long categoriaId, String texto) {
        return repo.buscar(categoriaId, texto).stream().map(TemaResponse::de).toList();
    }

    @Transactional(readOnly = true)
    public TemaDetalleResponse detalle(Long id) {
        Tema tema = obtener(id);
        List<ComentarioResponse> comentarios = comentarioRepo.findByTemaIdOrderByIdAsc(id)
            .stream().map(ComentarioResponse::de).toList();
        return TemaDetalleResponse.de(tema, comentarios);
    }

    Tema obtener(Long id) {
        return repo.findById(id).orElseThrow(() ->
            new NegocioException(HttpStatus.NOT_FOUND, "Tema no encontrado: " + id));
    }

    @Transactional
    public TemaResponse crear(TemaRequest datos, UsuarioAutenticado autor) {
        categoriaService.obtener(datos.categoriaId());
        Tema nuevo = new Tema(null, datos.categoriaId(), autor.id(), autor.username(),
            datos.titulo(), datos.contenido(), Instant.now());
        Tema guardado = repo.save(nuevo);
        log.info("Tema creado id={} autor={}", guardado.getId(), autor.username());
        return TemaResponse.de(guardado);
    }

    @Transactional
    public TemaResponse actualizar(Long id, TemaRequest datos, UsuarioAutenticado autor) {
        Tema actual = obtener(id);
        exigirPropietario(actual.getAutorId(), autor);
        categoriaService.obtener(datos.categoriaId());
        actual.setCategoriaId(datos.categoriaId());
        actual.setTitulo(datos.titulo());
        actual.setContenido(datos.contenido());
        Tema guardado = repo.save(actual);
        log.info("Tema actualizado id={}", id);
        return TemaResponse.de(guardado);
    }

    @Transactional
    public void eliminar(Long id, UsuarioAutenticado autor) {
        Tema actual = obtener(id);
        exigirPropietario(actual.getAutorId(), autor);
        // ON DELETE CASCADE en Oracle elimina sus comentarios.
        repo.delete(actual);
        log.info("Tema eliminado id={} (y sus comentarios)", id);
    }

    static void exigirPropietario(Long autorId, UsuarioAutenticado autor) {
        if (!autorId.equals(autor.id())) {
            throw new NegocioException(HttpStatus.FORBIDDEN, "Solo el autor puede modificar o eliminar este contenido");
        }
    }
}
