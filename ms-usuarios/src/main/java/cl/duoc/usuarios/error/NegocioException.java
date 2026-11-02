package cl.duoc.usuarios.error;

import org.springframework.http.HttpStatus;

/** Excepcion de negocio con el estado HTTP que debe devolverse. */
public class NegocioException extends RuntimeException {
    private final HttpStatus estado;

    public NegocioException(HttpStatus estado, String mensaje) {
        super(mensaje);
        this.estado = estado;
    }

    public HttpStatus estado() { return estado; }
}
