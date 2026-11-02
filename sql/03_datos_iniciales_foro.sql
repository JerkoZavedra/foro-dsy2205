
INSERT INTO CATEGORIAS (NOMBRE, DESCRIPCION)
SELECT 'Tecnologia', 'Programacion, hardware, software y noticias tecnologicas'
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM CATEGORIAS WHERE NOMBRE = 'Tecnologia');

INSERT INTO CATEGORIAS (NOMBRE, DESCRIPCION)
SELECT 'Videojuegos', 'Reseñas, lanzamientos y comunidad de videojuegos'
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM CATEGORIAS WHERE NOMBRE = 'Videojuegos');

INSERT INTO CATEGORIAS (NOMBRE, DESCRIPCION)
SELECT 'Musica', 'Artistas, generos y recomendaciones musicales'
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM CATEGORIAS WHERE NOMBRE = 'Musica');

INSERT INTO CATEGORIAS (NOMBRE, DESCRIPCION)
SELECT 'Cine y Series', 'Peliculas, series y novedades del streaming'
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM CATEGORIAS WHERE NOMBRE = 'Cine y Series');

INSERT INTO CATEGORIAS (NOMBRE, DESCRIPCION)
SELECT 'Deportes', 'Futbol, otros deportes y resultados de torneos'
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM CATEGORIAS WHERE NOMBRE = 'Deportes');

INSERT INTO CATEGORIAS (NOMBRE, DESCRIPCION)
SELECT 'Vida Universitaria', 'Estudio, trabajos y experiencias en Duoc UC'
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM CATEGORIAS WHERE NOMBRE = 'Vida Universitaria');

COMMIT;

-- ---------------------------------------------------------------------
-- 2. TEMAS (5, al menos 3 exigidos por el enunciado)
--    AUTOR_ID y AUTOR_USERNAME se copian desde USUARIOS en este
--    instante; de ahi en adelante viven solo en ms-foro.
-- ---------------------------------------------------------------------
INSERT INTO TEMAS (CATEGORIA_ID, AUTOR_ID, AUTOR_USERNAME, TITULO, CONTENIDO, FECHA_CREACION)
SELECT c.ID, u.ID, u.USERNAME,
       '¿Cual es el mejor lenguaje para empezar en 2026?',
       'Estoy evaluando por donde partir este semestre: Java, Python o Kotlin. ¿Que recomiendan para un backend simple?',
       SYSTIMESTAMP
FROM CATEGORIAS c, USUARIOS u
WHERE c.NOMBRE = 'Tecnologia' AND u.USERNAME = 'admin'
  AND NOT EXISTS (SELECT 1 FROM TEMAS WHERE TITULO = '¿Cual es el mejor lenguaje para empezar en 2026?');

INSERT INTO TEMAS (CATEGORIA_ID, AUTOR_ID, AUTOR_USERNAME, TITULO, CONTENIDO, FECHA_CREACION)
SELECT c.ID, u.ID, u.USERNAME,
       'Recomendaciones de juegos indie',
       'Busco juegos indie cortos para jugar entre clases. ¿Que me recomiendan de los ultimos dos años?',
       SYSTIMESTAMP
FROM CATEGORIAS c, USUARIOS u
WHERE c.NOMBRE = 'Videojuegos' AND u.USERNAME = 'usuario1'
  AND NOT EXISTS (SELECT 1 FROM TEMAS WHERE TITULO = 'Recomendaciones de juegos indie');

INSERT INTO TEMAS (CATEGORIA_ID, AUTOR_ID, AUTOR_USERNAME, TITULO, CONTENIDO, FECHA_CREACION)
SELECT c.ID, u.ID, u.USERNAME,
       'Discos imprescindibles de los 90',
       'Armando una lista de discos clasicos de los 90 para descubrir. ¿Cuales no pueden faltar?',
       SYSTIMESTAMP
FROM CATEGORIAS c, USUARIOS u
WHERE c.NOMBRE = 'Musica' AND u.USERNAME = 'usuario2'
  AND NOT EXISTS (SELECT 1 FROM TEMAS WHERE TITULO = 'Discos imprescindibles de los 90');

INSERT INTO TEMAS (CATEGORIA_ID, AUTOR_ID, AUTOR_USERNAME, TITULO, CONTENIDO, FECHA_CREACION)
SELECT c.ID, u.ID, u.USERNAME,
       'Mundial de clubes: favoritos',
       'Despues de la primera ronda, ¿cual creen que es el equipo favorito para quedarse con el titulo?',
       SYSTIMESTAMP
FROM CATEGORIAS c, USUARIOS u
WHERE c.NOMBRE = 'Deportes' AND u.USERNAME = 'usuario1'
  AND NOT EXISTS (SELECT 1 FROM TEMAS WHERE TITULO = 'Mundial de clubes: favoritos');

INSERT INTO TEMAS (CATEGORIA_ID, AUTOR_ID, AUTOR_USERNAME, TITULO, CONTENIDO, FECHA_CREACION)
SELECT c.ID, u.ID, u.USERNAME,
       'Series que deberias ver este año',
       'Comparto tres series que me gustaron este año y quiero que me recomienden otras parecidas.',
       SYSTIMESTAMP
FROM CATEGORIAS c, USUARIOS u
WHERE c.NOMBRE = 'Cine y Series' AND u.USERNAME = 'admin'
  AND NOT EXISTS (SELECT 1 FROM TEMAS WHERE TITULO = 'Series que deberias ver este año');

COMMIT;

-- ---------------------------------------------------------------------
-- 3. COMENTARIOS (6, al menos 3 exigidos por el enunciado)
-- ---------------------------------------------------------------------
INSERT INTO COMENTARIOS (TEMA_ID, AUTOR_ID, AUTOR_USERNAME, CONTENIDO, FECHA_CREACION)
SELECT t.ID, u.ID, u.USERNAME,
       'Yo partiria con Java: tiene mucho material de Duoc y tipado fuerte ayuda a entender errores.',
       SYSTIMESTAMP
FROM TEMAS t, USUARIOS u
WHERE t.TITULO = '¿Cual es el mejor lenguaje para empezar en 2026?' AND u.USERNAME = 'usuario2'
  AND NOT EXISTS (
    SELECT 1 FROM COMENTARIOS c WHERE c.TEMA_ID = t.ID AND c.AUTOR_USERNAME = 'usuario2'
      AND c.CONTENIDO LIKE 'Yo partiria con Java%');

INSERT INTO COMENTARIOS (TEMA_ID, AUTOR_ID, AUTOR_USERNAME, CONTENIDO, FECHA_CREACION)
SELECT t.ID, u.ID, u.USERNAME,
       'Python es mas simple al inicio, pero para backend con Spring igual vas a necesitar Java.',
       SYSTIMESTAMP
FROM TEMAS t, USUARIOS u
WHERE t.TITULO = '¿Cual es el mejor lenguaje para empezar en 2026?' AND u.USERNAME = 'usuario1'
  AND NOT EXISTS (
    SELECT 1 FROM COMENTARIOS c WHERE c.TEMA_ID = t.ID AND c.AUTOR_USERNAME = 'usuario1'
      AND c.CONTENIDO LIKE 'Python es mas simple%');

INSERT INTO COMENTARIOS (TEMA_ID, AUTOR_ID, AUTOR_USERNAME, CONTENIDO, FECHA_CREACION)
SELECT t.ID, u.ID, u.USERNAME,
       'Te recomiendo revisar el catalogo indie de las ultimas rebajas, hay varios de menos de 3 horas.',
       SYSTIMESTAMP
FROM TEMAS t, USUARIOS u
WHERE t.TITULO = 'Recomendaciones de juegos indie' AND u.USERNAME = 'admin'
  AND NOT EXISTS (
    SELECT 1 FROM COMENTARIOS c WHERE c.TEMA_ID = t.ID AND c.AUTOR_USERNAME = 'admin'
      AND c.CONTENIDO LIKE 'Te recomiendo revisar%');

INSERT INTO COMENTARIOS (TEMA_ID, AUTOR_ID, AUTOR_USERNAME, CONTENIDO, FECHA_CREACION)
SELECT t.ID, u.ID, u.USERNAME,
       'Cualquier lista de los 90 deberia incluir al menos un disco de rock alternativo.',
       SYSTIMESTAMP
FROM TEMAS t, USUARIOS u
WHERE t.TITULO = 'Discos imprescindibles de los 90' AND u.USERNAME = 'usuario1'
  AND NOT EXISTS (
    SELECT 1 FROM COMENTARIOS c WHERE c.TEMA_ID = t.ID AND c.AUTOR_USERNAME = 'usuario1'
      AND c.CONTENIDO LIKE 'Cualquier lista%');

INSERT INTO COMENTARIOS (TEMA_ID, AUTOR_ID, AUTOR_USERNAME, CONTENIDO, FECHA_CREACION)
SELECT t.ID, u.ID, u.USERNAME,
       'Coincido, aunque igual hay que ver como llegan de animo despues de la fase anterior.',
       SYSTIMESTAMP
FROM TEMAS t, USUARIOS u
WHERE t.TITULO = 'Mundial de clubes: favoritos' AND u.USERNAME = 'admin'
  AND NOT EXISTS (
    SELECT 1 FROM COMENTARIOS c WHERE c.TEMA_ID = t.ID AND c.AUTOR_USERNAME = 'admin'
      AND c.CONTENIDO LIKE 'Coincido, aunque igual%');

INSERT INTO COMENTARIOS (TEMA_ID, AUTOR_ID, AUTOR_USERNAME, CONTENIDO, FECHA_CREACION)
SELECT t.ID, u.ID, u.USERNAME,
       'Agrega subtitulos en español latino si puedes, se disfrutan mas asi.',
       SYSTIMESTAMP
FROM TEMAS t, USUARIOS u
WHERE t.TITULO = 'Series que deberias ver este año' AND u.USERNAME = 'usuario2'
  AND NOT EXISTS (
    SELECT 1 FROM COMENTARIOS c WHERE c.TEMA_ID = t.ID AND c.AUTOR_USERNAME = 'usuario2'
      AND c.CONTENIDO LIKE 'Agrega subtitulos%');

COMMIT;

-- ---------------------------------------------------------------------
-- 4. Verificacion rapida de cantidades
-- ---------------------------------------------------------------------
SELECT COUNT(*) AS TOTAL_CATEGORIAS  FROM CATEGORIAS;
SELECT COUNT(*) AS TOTAL_TEMAS       FROM TEMAS;
SELECT COUNT(*) AS TOTAL_COMENTARIOS FROM COMENTARIOS;
