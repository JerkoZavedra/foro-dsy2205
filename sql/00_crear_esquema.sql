-- =====================================================================
-- FORO EN LINEA - Paso 0: crear el esquema/usuario Oracle local
-- Ejecutar UNA SOLA VEZ, conectado como usuario con privilegios DBA
-- (por ejemplo SYSTEM o SYS) sobre el PDB local de Oracle.
--
-- NO ES NECESARIO si usas el contenedor Docker descrito en el README
-- (imagen gvenzl/oracle-free con APP_USER=FORO_APP): ese contenedor ya
-- crea este usuario automaticamente, con el rol DB_DEVELOPER_ROLE (que
-- incluye los mismos privilegios otorgados mas abajo). Este script queda
-- como referencia para una instalacion tradicional de Oracle (XE u otra),
-- donde el PDB puede llamarse XEPDB1, FREEPDB1 u otro nombre: revisa
-- "SELECT name FROM v$pdbs;" antes de asumirlo.
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
