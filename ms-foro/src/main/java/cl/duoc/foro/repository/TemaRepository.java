package cl.duoc.foro.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import cl.duoc.foro.model.Tema;

public interface TemaRepository extends JpaRepository<Tema, Long> {

    /**
     * Busqueda sencilla: cada filtro es opcional (NULL = no se aplica).
     * texto compara, sin distinguir mayusculas, contra titulo y contenido.
     */
    @Query("""
        SELECT t FROM Tema t
        WHERE (:categoriaId IS NULL OR t.categoriaId = :categoriaId)
          AND (:texto IS NULL
               OR LOWER(t.titulo) LIKE LOWER(CONCAT('%', :texto, '%'))
               OR LOWER(t.contenido) LIKE LOWER(CONCAT('%', :texto, '%')))
        ORDER BY t.id DESC
        """)
    List<Tema> buscar(@Param("categoriaId") Long categoriaId, @Param("texto") String texto);
}
