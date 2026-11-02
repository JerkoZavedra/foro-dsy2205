package cl.duoc.foro.controller;

import java.util.List;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import cl.duoc.foro.dto.ComentarioRequest;
import cl.duoc.foro.dto.ComentarioResponse;
import cl.duoc.foro.dto.TemaDetalleResponse;
import cl.duoc.foro.dto.TemaRequest;
import cl.duoc.foro.dto.TemaResponse;
import cl.duoc.foro.security.UsuarioAutenticado;
import cl.duoc.foro.service.ComentarioService;
import cl.duoc.foro.service.TemaService;

/**
 * GET es publico (busqueda y detalle). POST requiere cualquier usuario
 * autenticado (ADMIN o USUARIO). PUT/DELETE solo el autor del tema
 * (el Service verifica la propiedad; aqui solo se exige autenticacion).
 */
@RestController
@RequestMapping("/api/temas")
public class TemaController {
    private final TemaService servicio;
    private final ComentarioService comentarioServicio;

    public TemaController(TemaService servicio, ComentarioService comentarioServicio) {
        this.servicio = servicio;
        this.comentarioServicio = comentarioServicio;
    }

    /** GET /api/temas?categoriaId=&texto= : lista, busca por texto y/o filtra por categoria. */
    @GetMapping
    public List<TemaResponse> buscar(@RequestParam(required = false) Long categoriaId,
                                      @RequestParam(required = false) String texto) {
        return servicio.buscar(categoriaId, texto);
    }

    @GetMapping("/{id}")
    public TemaDetalleResponse detalle(@PathVariable Long id) {
        return servicio.detalle(id);
    }

    @PostMapping
    public ResponseEntity<TemaResponse> crear(@Valid @RequestBody TemaRequest datos,
                                               @AuthenticationPrincipal UsuarioAutenticado autor) {
        return ResponseEntity.status(HttpStatus.CREATED).body(servicio.crear(datos, autor));
    }

    @PutMapping("/{id}")
    public TemaResponse actualizar(@PathVariable Long id, @Valid @RequestBody TemaRequest datos,
                                    @AuthenticationPrincipal UsuarioAutenticado autor) {
        return servicio.actualizar(id, datos, autor);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id,
                                          @AuthenticationPrincipal UsuarioAutenticado autor) {
        servicio.eliminar(id, autor);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{temaId}/comentarios")
    public List<ComentarioResponse> listarComentarios(@PathVariable Long temaId) {
        return comentarioServicio.listarPorTema(temaId);
    }

    @PostMapping("/{temaId}/comentarios")
    public ResponseEntity<ComentarioResponse> comentar(@PathVariable Long temaId,
                                                         @Valid @RequestBody ComentarioRequest datos,
                                                         @AuthenticationPrincipal UsuarioAutenticado autor) {
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(comentarioServicio.crear(temaId, datos, autor));
    }
}
