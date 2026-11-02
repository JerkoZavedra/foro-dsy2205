package cl.duoc.foro.model;

import java.time.LocalDateTime;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/** Igual que en Tema: AUTOR_ID/AUTOR_USERNAME son una copia, sin FK a USUARIOS. */
@Entity
@Table(name = "COMENTARIOS")
public class Comentario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @Column(name = "TEMA_ID", nullable = false)
    private Long temaId;

    @Column(name = "AUTOR_ID", nullable = false)
    private Long autorId;

    @Column(name = "AUTOR_USERNAME", nullable = false, length = 50)
    private String autorUsername;

    @Column(name = "CONTENIDO", nullable = false, length = 2000)
    private String contenido;

    @Column(name = "FECHA_CREACION", nullable = false)
    private LocalDateTime fechaCreacion;

    public Comentario() {
        // Constructor sin argumentos requerido por JPA.
    }

    public Comentario(Long id, Long temaId, Long autorId, String autorUsername,
                       String contenido, LocalDateTime fechaCreacion) {
        this.id = id;
        this.temaId = temaId;
        this.autorId = autorId;
        this.autorUsername = autorUsername;
        this.contenido = contenido;
        this.fechaCreacion = fechaCreacion;
    }

    public Long getId() { return id; }
    public Long getTemaId() { return temaId; }
    public Long getAutorId() { return autorId; }
    public String getAutorUsername() { return autorUsername; }
    public String getContenido() { return contenido; }
    public LocalDateTime getFechaCreacion() { return fechaCreacion; }

    public void setId(Long id) { this.id = id; }
    public void setTemaId(Long temaId) { this.temaId = temaId; }
    public void setAutorId(Long autorId) { this.autorId = autorId; }
    public void setAutorUsername(String autorUsername) { this.autorUsername = autorUsername; }
    public void setContenido(String contenido) { this.contenido = contenido; }
    public void setFechaCreacion(LocalDateTime fechaCreacion) { this.fechaCreacion = fechaCreacion; }
}
