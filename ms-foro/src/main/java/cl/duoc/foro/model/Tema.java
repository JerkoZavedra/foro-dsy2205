package cl.duoc.foro.model;

import java.time.LocalDateTime;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * AUTOR_ID y AUTOR_USERNAME son una copia de la identidad resuelta por
 * ms-usuarios al momento de publicar; esta tabla NUNCA referencia la tabla
 * USUARIOS de ese otro microservicio (ver sql/02_crear_tablas_foro.sql).
 */
@Entity
@Table(name = "TEMAS")
public class Tema {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @Column(name = "CATEGORIA_ID", nullable = false)
    private Long categoriaId;

    @Column(name = "AUTOR_ID", nullable = false)
    private Long autorId;

    @Column(name = "AUTOR_USERNAME", nullable = false, length = 50)
    private String autorUsername;

    @Column(name = "TITULO", nullable = false, length = 150)
    private String titulo;

    @Column(name = "CONTENIDO", nullable = false, length = 4000)
    private String contenido;

    @Column(name = "FECHA_CREACION", nullable = false)
    private LocalDateTime fechaCreacion;

    public Tema() {
        // Constructor sin argumentos requerido por JPA.
    }

    public Tema(Long id, Long categoriaId, Long autorId, String autorUsername,
                String titulo, String contenido, LocalDateTime fechaCreacion) {
        this.id = id;
        this.categoriaId = categoriaId;
        this.autorId = autorId;
        this.autorUsername = autorUsername;
        this.titulo = titulo;
        this.contenido = contenido;
        this.fechaCreacion = fechaCreacion;
    }

    public Long getId() { return id; }
    public Long getCategoriaId() { return categoriaId; }
    public Long getAutorId() { return autorId; }
    public String getAutorUsername() { return autorUsername; }
    public String getTitulo() { return titulo; }
    public String getContenido() { return contenido; }
    public LocalDateTime getFechaCreacion() { return fechaCreacion; }

    public void setId(Long id) { this.id = id; }
    public void setCategoriaId(Long categoriaId) { this.categoriaId = categoriaId; }
    public void setAutorId(Long autorId) { this.autorId = autorId; }
    public void setAutorUsername(String autorUsername) { this.autorUsername = autorUsername; }
    public void setTitulo(String titulo) { this.titulo = titulo; }
    public void setContenido(String contenido) { this.contenido = contenido; }
    public void setFechaCreacion(LocalDateTime fechaCreacion) { this.fechaCreacion = fechaCreacion; }
}
