package cl.duoc.foro.service;

import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import cl.duoc.foro.dto.CategoriaResponse;
import cl.duoc.foro.error.NegocioException;
import cl.duoc.foro.model.Categoria;
import cl.duoc.foro.repository.CategoriaRepository;

/** Categorias de solo lectura por API: se cargan una vez con sql/03_datos_iniciales_foro.sql. */
@Service
public class CategoriaService {
    private final CategoriaRepository repo;

    public CategoriaService(CategoriaRepository repo) {
        this.repo = repo;
    }

    @Transactional(readOnly = true)
    public List<CategoriaResponse> listar() {
        return repo.findAll().stream().map(CategoriaResponse::de).toList();
    }

    @Transactional(readOnly = true)
    public CategoriaResponse buscar(Long id) {
        return CategoriaResponse.de(obtener(id));
    }

    Categoria obtener(Long id) {
        return repo.findById(id).orElseThrow(() ->
            new NegocioException(HttpStatus.NOT_FOUND, "Categoria no encontrada: " + id));
    }
}
