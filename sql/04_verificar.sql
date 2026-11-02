-- =====================================================================
-- FORO EN LINEA - Consultas de verificacion manual (SQL Developer)
-- Usar durante y despues de las pruebas Postman. NUNCA mostrar
-- PASS_HASH en una evidencia o grabacion.
-- =====================================================================

-- Cuentas (sin PASS_HASH)
SELECT ID, USERNAME, EMAIL, ROL FROM USUARIOS ORDER BY ID;
SELECT ROL, COUNT(*) AS CANTIDAD FROM USUARIOS GROUP BY ROL;

-- Categorias
SELECT ID, NOMBRE, DESCRIPCION FROM CATEGORIAS ORDER BY ID;

-- Temas
SELECT ID, CATEGORIA_ID, AUTOR_ID, AUTOR_USERNAME, TITULO, FECHA_CREACION
FROM TEMAS ORDER BY ID;

-- Comentarios
SELECT ID, TEMA_ID, AUTOR_ID, AUTOR_USERNAME, CONTENIDO, FECHA_CREACION
FROM COMENTARIOS ORDER BY ID;

-- Comprobar persistencia tras reiniciar ambos microservicios:
-- ejecutar estas mismas consultas antes y despues del reinicio y
-- comparar que las filas (incluidas las creadas por Postman) sigan ahi.
-- No ejecutar TRUNCATE ni DROP sobre estas tablas.
