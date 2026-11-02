package cl.duoc.foro.error;

import org.springframework.http.HttpStatus;

public class NegocioException extends RuntimeException {
    private final HttpStatus estado;

    public NegocioException(HttpStatus estado, String mensaje) {
        super(mensaje);
        this.estado = estado;
    }

    public HttpStatus estado() { return estado; }
}
