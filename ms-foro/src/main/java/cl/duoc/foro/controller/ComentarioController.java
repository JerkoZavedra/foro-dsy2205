package cl.duoc.foro.controller;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import cl.duoc.foro.dto.ComentarioRequest;
import cl.duoc.foro.dto.ComentarioResponse;
import cl.duoc.foro.security.UsuarioAutenticado;
import cl.duoc.foro.service.ComentarioService;

/** Consulta, modificacion y eliminacion de un comentario puntual por su propio ID. */
@RestController
@RequestMapping("/api/comentarios")
public class ComentarioController {
    private final ComentarioService servicio;

    public ComentarioController(ComentarioService servicio) {
        this.servicio = servicio;
    }

    @GetMapping("/{id}")
    public ComentarioResponse buscar(@PathVariable Long id) {
        return servicio.buscar(id);
    }

    @PutMapping("/{id}")
    public ComentarioResponse actualizar(@PathVariable Long id, @Valid @RequestBody ComentarioRequest datos,
                                          @AuthenticationPrincipal UsuarioAutenticado autor) {
        return servicio.actualizar(id, datos, autor);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id,
                                          @AuthenticationPrincipal UsuarioAutenticado autor) {
        servicio.eliminar(id, autor);
        return ResponseEntity.noContent().build();
    }
}
