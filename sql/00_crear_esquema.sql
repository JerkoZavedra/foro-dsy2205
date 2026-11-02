-- =====================================================================
-- FORO EN LINEA - Paso 0: crear el esquema/usuario Oracle local
-- Ejecutar UNA SOLA VEZ, conectado como usuario con privilegios DBA
-- (por ejemplo SYSTEM) sobre el PDB local XEPDB1 de Oracle XE.
-- =====================================================================

-- 1. Crear el usuario/esquema que usaran ambos microservicios.
--    Reemplazar 'CAMBIAR_CLAVE' por una clave propia antes de ejecutar
--    y NUNCA dejar esa clave real en un archivo versionado en Git.
CREATE USER FORO_APP IDENTIFIED BY "CAMBIAR_CLAVE"
    DEFAULT TABLESPACE USERS
    TEMPORARY TABLESPACE TEMP
    QUOTA UNLIMITED ON USERS;

-- 2. Privilegios minimos para que la aplicacion se conecte y administre
--    sus propias tablas (sin privilegios de administracion de la BD).
GRANT CREATE SESSION TO FORO_APP;
GRANT CREATE TABLE   TO FORO_APP;
GRANT CREATE SEQUENCE TO FORO_APP;
GRANT CREATE VIEW    TO FORO_APP;

-- A partir de aqui, conectarse como FORO_APP (no como SYSTEM) para
-- ejecutar 01_crear_tablas_usuarios.sql, 02_crear_tablas_foro.sql,
-- 03_datos_iniciales_foro.sql y 04_verificar.sql.
