-- =====================================================================
-- FORO EN LINEA - ms-foro
-- Tablas: CATEGORIAS, TEMAS, COMENTARIOS
-- Ejecutar UNA SOLA VEZ, conectado como FORO_APP, despues de
-- 01_crear_tablas_usuarios.sql.
--
-- Diseno de integridad (coherencia simple, sin sistemas complejos):
-- - TEMAS.CATEGORIA_ID es FK real hacia CATEGORIAS: ambas tablas
--   pertenecen al mismo microservicio (ms-foro), por lo que la FK es
--   apropiada. Solo se permite borrar una categoria sin temas.
-- - COMENTARIOS.TEMA_ID es FK real hacia TEMAS con ON DELETE CASCADE:
--   al eliminar un tema se eliminan automaticamente sus comentarios.
--   Esta es la regla explicita para "eliminar un tema con comentarios".
-- - TEMAS.AUTOR_ID y COMENTARIOS.AUTOR_ID son NUMBER simples, SIN FK
--   hacia USUARIOS: esa tabla pertenece a OTRO microservicio
--   (ms-usuarios) y el foro no debe depender de su esquema interno ni
--   consultarla directamente para autenticar. AUTOR_USERNAME guarda una
--   copia del nombre de usuario al momento de publicar. Esta es la
--   regla explicita para "eliminar un usuario con publicaciones": sus
--   temas y comentarios se conservan con el nombre guardado, incluso si
--   la cuenta se elimina despues en ms-usuarios.
-- =====================================================================

CREATE TABLE CATEGORIAS (
    ID          NUMBER(19) GENERATED ALWAYS AS IDENTITY,
    NOMBRE      VARCHAR2(60 CHAR)  NOT NULL,
    DESCRIPCION VARCHAR2(255 CHAR),
    CONSTRAINT PK_CATEGORIAS PRIMARY KEY (ID),
    CONSTRAINT UQ_CATEGORIAS_NOMBRE UNIQUE (NOMBRE)
);

CREATE TABLE TEMAS (
    ID              NUMBER(19) GENERATED ALWAYS AS IDENTITY,
    CATEGORIA_ID    NUMBER(19) NOT NULL,
    AUTOR_ID        NUMBER(19) NOT NULL,
    AUTOR_USERNAME  VARCHAR2(50 CHAR) NOT NULL,
    TITULO          VARCHAR2(150 CHAR) NOT NULL,
    CONTENIDO       VARCHAR2(4000 CHAR) NOT NULL,
    FECHA_CREACION  TIMESTAMP NOT NULL,
    CONSTRAINT PK_TEMAS PRIMARY KEY (ID),
    CONSTRAINT FK_TEMAS_CATEGORIA FOREIGN KEY (CATEGORIA_ID)
        REFERENCES CATEGORIAS (ID)
);

CREATE TABLE COMENTARIOS (
    ID              NUMBER(19) GENERATED ALWAYS AS IDENTITY,
    TEMA_ID         NUMBER(19) NOT NULL,
    AUTOR_ID        NUMBER(19) NOT NULL,
    AUTOR_USERNAME  VARCHAR2(50 CHAR) NOT NULL,
    CONTENIDO       VARCHAR2(2000 CHAR) NOT NULL,
    FECHA_CREACION  TIMESTAMP NOT NULL,
    CONSTRAINT PK_COMENTARIOS PRIMARY KEY (ID),
    CONSTRAINT FK_COMENTARIOS_TEMA FOREIGN KEY (TEMA_ID)
        REFERENCES TEMAS (ID) ON DELETE CASCADE
);

CREATE INDEX IX_TEMAS_CATEGORIA ON TEMAS (CATEGORIA_ID);
CREATE INDEX IX_COMENTARIOS_TEMA ON COMENTARIOS (TEMA_ID);
