package cl.duoc.usuarios.controller;

import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/** Comprobacion publica de que el proceso esta arriba (sin exponer datos). */
@RestController
public class EstadoController {
    @GetMapping("/api/estado")
    public Map<String, String> estado() {
        return Map.of("servicio", "ms-usuarios", "estado", "arriba");
    }
}
