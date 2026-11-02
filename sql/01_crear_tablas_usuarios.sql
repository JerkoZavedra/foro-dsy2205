-- =====================================================================
-- FORO EN LINEA - ms-usuarios
-- Tabla: USUARIOS
-- Ejecutar UNA SOLA VEZ, conectado como FORO_APP.
-- spring.jpa.hibernate.ddl-auto=validate: Hibernate NUNCA crea ni altera
-- esta tabla; solo valida que coincida con la entidad Usuario.
-- =====================================================================

CREATE TABLE USUARIOS (
    ID         NUMBER(19) GENERATED ALWAYS AS IDENTITY,
    USERNAME   VARCHAR2(50 CHAR)  NOT NULL,
    EMAIL      VARCHAR2(120 CHAR) NOT NULL,
    PASS_HASH  VARCHAR2(100 CHAR) NOT NULL,
    ROL        VARCHAR2(20 CHAR)  NOT NULL,
    CONSTRAINT PK_USUARIOS   PRIMARY KEY (ID),
    CONSTRAINT UQ_USUARIOS_USERNAME UNIQUE (USERNAME),
    CONSTRAINT UQ_USUARIOS_EMAIL    UNIQUE (EMAIL),
    CONSTRAINT CK_USUARIOS_ROL CHECK (ROL IN ('ADMIN','USUARIO'))
);

-- Las tres cuentas iniciales (1 ADMIN + 2 USUARIO) NO se insertan aqui:
-- ms-usuarios las crea solo la primera vez que arranca y la tabla esta
-- vacia (ver config.CuentasIniciales), usando BCrypt y claves que se
-- entregan por variables de entorno. Asi se cumple "no inventar hashes
-- BCrypt ni guardar contrasenas reales en el codigo o en el script".
