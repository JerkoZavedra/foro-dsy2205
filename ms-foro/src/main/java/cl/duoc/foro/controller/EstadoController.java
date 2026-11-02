package cl.duoc.foro.controller;

import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class EstadoController {
    @GetMapping("/api/estado")
    public Map<String, String> estado() {
        return Map.of("servicio", "ms-foro", "estado", "arriba");
    }
}
