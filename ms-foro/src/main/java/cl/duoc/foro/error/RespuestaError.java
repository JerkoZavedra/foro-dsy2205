package cl.duoc.foro.error;

import java.time.Instant;
import java.util.Map;

/** Contrato estable de errores, igual en ambos microservicios. */
public record RespuestaError(Instant momento, int estado, String mensaje,
                              String ruta, Map<String, String> campos) {}
