package cl.duoc.foro.dto;

import cl.duoc.foro.model.Categoria;

public record CategoriaResponse(Long id, String nombre, String descripcion) {
    public static CategoriaResponse de(Categoria c) {
        return new CategoriaResponse(c.getId(), c.getNombre(), c.getDescripcion());
    }
}
