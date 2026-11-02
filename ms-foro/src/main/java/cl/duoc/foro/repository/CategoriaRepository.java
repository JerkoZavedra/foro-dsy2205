package cl.duoc.foro.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import cl.duoc.foro.model.Categoria;

public interface CategoriaRepository extends JpaRepository<Categoria, Long> {
}
