package cl.duoc.foro.error;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ManejadorErrores {

    @ExceptionHandler(NegocioException.class)
    public ResponseEntity<RespuestaError> negocio(NegocioException e, HttpServletRequest r) {
        return responder(e.estado(), e.getMessage(), r, Map.of());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<RespuestaError> validacion(MethodArgumentNotValidException e,
                                                       HttpServletRequest r) {
        Map<String, String> errores = new LinkedHashMap<>();
        e.getBindingResult().getFieldErrors().forEach(f ->
            errores.putIfAbsent(f.getField(), f.getDefaultMessage()));
        return responder(HttpStatus.BAD_REQUEST, "Solicitud invalida", r, errores);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<RespuestaError> jsonIncorrecto(Exception e, HttpServletRequest r) {
        return responder(HttpStatus.BAD_REQUEST, "JSON incorrecto", r, Map.of());
    }

    private ResponseEntity<RespuestaError> responder(HttpStatus status, String msj,
                                                       HttpServletRequest r, Map<String, String> campos) {
        return ResponseEntity.status(status).body(new RespuestaError(
            Instant.now(), status.value(), msj, r.getRequestURI(), campos));
    }
}
