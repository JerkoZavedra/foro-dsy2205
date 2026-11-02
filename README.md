# Foro en Línea — DSY2205, Experiencia 1, Semana 2

Evaluación individual: backend de un sistema de foros en línea, compuesto por
dos microservicios Spring Boot independientes y una base de datos Oracle
local. Desarrollado siguiendo el documento oficial
`DSY2205_Exp1_S2_Instrucciones_especificas_B.docx` y usando como referencia
técnica y de organización el material de ejemplo del profesor (`FS3_S2_DP`,
catálogo DuocPlay + `dp-usuarios`), sin modificar ese material original.

## 1. Alcance

Se desarrollan **solo 2 de los 3 microservicios posibles** del caso, como
permite el enunciado:

| Microservicio | Puerto | Responsabilidad |
|---|---|---|
| `ms-usuarios` | 8081 | Crear, consultar, modificar, eliminar usuarios e iniciar sesión |
| `ms-foro`     | 8080 | Consultar categorías, listar/buscar/ver temas, publicar temas y comentarios |

**No se implementa** el tercer microservicio (moderación/baneo), frontend,
pagos, notificaciones, archivos adjuntos, recuperación de contraseña, JWT,
API Gateway ni descubrimiento de servicios: no son necesarios para cumplir
la actividad y el enunciado pide desarrollar solo 2 microservicios sin
frontend.

## 2. Arquitectura

```
Postman ──► ms-foro (8080) ──HTTP Basic──► ms-usuarios (8081)
                │                                │
                └────────────► Oracle XE / XEPDB1 (esquema FORO_APP) ◄───┘
                     tablas: CATEGORIAS, TEMAS, COMENTARIOS         tabla: USUARIOS
```

- Un solo Oracle local y un solo esquema (`FORO_APP`), con tablas separadas
  por responsabilidad, tal como permite el enunciado para simplificar.
- `ms-foro` **nunca** consulta la tabla `USUARIOS` para autenticar: reenvía
  las credenciales HTTP Basic que recibe a `ms-usuarios`
  (`GET /api/auth/perfil`) y usa la identidad que éste confirma. Así, cada
  microservicio administra solo sus propias tablas con Spring Data JPA.
- Login HTTP Basic: no se emite token ni se mantiene sesión. Cada petición
  envía usuario y contraseña; Spring Security valida el hash BCrypt en cada
  llamada. Esto es intencional y se explica más abajo (sección 7).

## 3. Tecnologías y versiones

Java 21, Spring Boot **4.1.1** (`spring-boot-starter-webmvc`, `-data-jpa`,
`-validation`, `-security`), Maven Wrapper, driver `ojdbc11`, Oracle
XE/XEPDB1, Postman.

Estas son las mismas versiones del ejemplo del profesor. **Se comprobó
realmente** (no solo por inspección) que `spring-boot-starter-parent:4.1.1`
y `spring-boot-starter-webmvc` existen y son compatibles con Java 21:
se compiló el proyecto `dp-usuarios` del ejemplo con Maven y JDK 21 en este
equipo, y terminó con éxito (`mvn compile`, `BUILD SUCCESS`). No fue
necesario cambiar ninguna versión.

## 4. Estructura de carpetas

```
FS3/
├── ms-usuarios/        Microservicio de usuarios (puerto 8081)
├── ms-foro/            Microservicio de foro (puerto 8080)
├── sql/                Scripts Oracle, en el orden en que deben ejecutarse
├── postman/            Colección y entorno Postman (sin credenciales reales)
├── docs/               Matriz de trazabilidad y guion de la demostración Kaltura
└── README.md           Este archivo
```

## 5. Configuración de Oracle local

Requiere una instalación de **Oracle Database XE** local con el PDB
`XEPDB1` (la misma distribución usada en el ejemplo del profesor). Con
SQL Developer u otra herramienta cliente, conectado como un usuario con
privilegios de administración (por ejemplo `SYSTEM`):

1. Ejecutar `sql/00_crear_esquema.sql` — crea el usuario/esquema
   `FORO_APP` (reemplazar `CAMBIAR_CLAVE` por una contraseña propia antes
   de ejecutar; no deja esa contraseña en ningún archivo del proyecto).
2. Conectarse como `FORO_APP` y ejecutar, en este orden:
   - `sql/01_crear_tablas_usuarios.sql`
   - `sql/02_crear_tablas_foro.sql`
3. **No ejecutar todavía** `sql/03_datos_iniciales_foro.sql`: necesita que
   `ms-usuarios` haya creado antes las 3 cuentas iniciales (paso 6).

`spring.jpa.hibernate.ddl-auto=validate` en ambos microservicios: Hibernate
nunca crea ni altera tablas, solo valida que coincidan con las entidades.

## 6. Variables de entorno

Ningún secreto se guarda en el código ni en los `application.properties`
(solo referencias `${VARIABLE}`). Antes de ejecutar, definir en la terminal
(cada proyecto tiene un `.env.example` ilustrativo; no se versiona ningún
`.env` real):

```bash
# comunes a ambos microservicios
export DB_USER=FORO_APP
export DB_PASSWORD="la_clave_que_definiste_en_00_crear_esquema"

# solo ms-usuarios: contraseñas de las 3 cuentas de demostración (8+ caracteres)
export FORO_ADMIN_PASS="clave_admin_demo"
export FORO_USUARIO1_PASS="clave_usuario1_demo"
export FORO_USUARIO2_PASS="clave_usuario2_demo"

# solo ms-foro (opcional si ms-usuarios corre en localhost:8081)
export FORO_USUARIOS_URL=http://localhost:8081
```

Si `FORO_ADMIN_PASS`/`FORO_USUARIO1_PASS`/`FORO_USUARIO2_PASS` no están
definidas, `ms-usuarios` arranca igual pero **no crea ninguna cuenta** y
deja un `WARN` explícito en el log indicando exactamente qué variables
faltan (no queda "a medias" en silencio).

## 7. Orden de ejecución

1. **Compilar** ambos proyectos (no requiere Oracle arriba):
   ```bash
   cd ms-usuarios && ./mvnw -q compile && cd ..
   cd ms-foro      && ./mvnw -q compile && cd ..
   ```
2. **Arrancar `ms-usuarios`** (con las variables de entorno del paso 6
   definidas en esa misma terminal):
   ```bash
   cd ms-usuarios && ./mvnw spring-boot:run
   ```
   En el primer arranque, con la tabla `USUARIOS` vacía, crea
   automáticamente 3 cuentas: `admin` (ADMIN), `usuario1` y `usuario2`
   (USUARIO), con las contraseñas de las variables de entorno, ya
   hasheadas con BCrypt. En arranques siguientes detecta que ya existen
   cuentas y no vuelve a insertarlas (no se duplican datos al reiniciar).
3. Con `ms-usuarios` arriba y las 3 cuentas creadas, ejecutar
   `sql/03_datos_iniciales_foro.sql` (conectado como `FORO_APP`): carga 6
   categorías, 5 temas y 6 comentarios ficticios, usando subconsultas por
   `USERNAME`/`NOMBRE`/`TITULO` para no asumir IDs fijos y sin duplicar si
   se ejecuta más de una vez por error.
4. **Arrancar `ms-foro`** en otra terminal:
   ```bash
   cd ms-foro && ./mvnw spring-boot:run
   ```
5. Probar con Postman (sección 8) y verificar en SQL Developer con
   `sql/04_verificar.sql`.

### Sobre el login HTTP Basic

`POST /api/auth/login` y `GET /api/auth/perfil` (en `ms-usuarios`) **no
generan un token ni abren una sesión**: Spring Security valida usuario y
contraseña (contra el hash BCrypt guardado) en cada petición HTTP, usando
el encabezado `Authorization: Basic ...`. "Iniciar sesión" aquí significa
"la petición demuestra credenciales válidas en el momento", no "el servidor
recuerda que ya inició sesión". Por eso cada llamada posterior (crear un
tema, editar un comentario, etc.) debe volver a enviar las mismas
credenciales. Es una decisión deliberada para mantener el alcance simple
(sin JWT ni gestión de sesiones), igual que en el ejemplo del profesor, y
solo es apropiada en `localhost` sin TLS — nunca para una red pública.

## 8. Pruebas con Postman

Ver `postman/README.txt`. La colección `Foro_Collection.json` cubre, en 7
carpetas ordenadas (00 a 06): estado de ambos servicios; login correcto e
incorrecto; diferencias de permisos ADMIN/USUARIO; CRUD completo de
usuarios incluida la protección del último ADMIN; consulta de categorías,
búsqueda y filtro de temas; publicación y edición de temas/comentarios
propios; intento de modificar contenido ajeno (403); datos inválidos,
duplicados y recursos inexistentes; y comprobación de persistencia tras
reiniciar. Los IDs se toman siempre de las respuestas reales (POST), nunca
se asumen fijos — con la única excepción del ADMIN semilla (id 1), usado
solo para confirmar que no puede eliminarse al último administrador.

## 9. Reglas de negocio explícitas

- **Último ADMIN protegido**: no se puede eliminar ni degradar de rol al
  último usuario ADMIN (`UsuarioService`, igual que en el ejemplo del
  profesor).
- **Eliminar un tema con comentarios**: se eliminan en cascada
  (`ON DELETE CASCADE` en `COMENTARIOS.TEMA_ID`, ver
  `sql/02_crear_tablas_foro.sql`). Regla simple y explícita a nivel de base
  de datos.
- **Eliminar un usuario con publicaciones**: `ms-foro` no tiene clave
  foránea hacia `USUARIOS` (son tablas de microservicios distintos). Sus
  temas y comentarios **se conservan**, mostrando el `AUTOR_USERNAME`
  copiado al momento de publicar, aunque la cuenta se elimine después en
  `ms-usuarios`. Es la alternativa simple que evita datos incoherentes sin
  construir un sistema de sincronización entre microservicios.
- **Autor real, nunca arbitrario**: al crear un tema o comentario, el autor
  se obtiene desde la identidad autenticada (`ms-usuarios` vía
  `UsuariosRemotosProvider`), no desde el JSON de la petición.
- **Modificar/eliminar solo contenido propio**: validado comparando el
  autor guardado con la identidad autenticada; si no coincide, `403`. No
  existe ninguna ruta de moderación ni de "admin edita cualquier post".

## 10. Qué se verificó realmente vs. qué queda pendiente

**Verificado en este equipo (no solo revisión estática):**
- `ms-usuarios` y `ms-foro` **compilan** con `./mvnw compile` (Spring Boot
  4.1.1, Java 21, `BUILD SUCCESS`).
- El `pom.xml` del ejemplo del profesor (`dp-usuarios`) también compila con
  las mismas versiones, confirmando que no hace falta cambiarlas.
- Los JSON de Postman (`Foro_Collection.json`, `Foro_Entorno.json`) son
  JSON válido (se cargaron con un parser).

**Pendiente de verificar en un equipo con Oracle XE instalado** (este
entorno de generación no tiene Oracle disponible ni acceso a Docker para
levantarlo):
- Ejecución real de `spring-boot:run` de ambos microservicios contra
  Oracle.
- Creación real de las tablas y carga de los datos iniciales con los
  scripts SQL.
- Ejecución real de la colección Postman end-to-end (200/201/204/400/401/
  403/404/409 tal como se documentan en los tests de cada request).
- Prueba de persistencia tras reiniciar ambos procesos.
- Grabación de la demostración en Kaltura (ver `docs/guion_kaltura.md`).

Antes de grabar o entregar: seguir la sección 5-8 de este README paso a
paso en un equipo con Oracle XE, confirmando cada punto de la lista
anterior.

## 11. Documentos adicionales

- [`docs/matriz_trazabilidad.md`](docs/matriz_trazabilidad.md): cada
  requisito oficial, su implementación concreta (archivo/endpoint) y la
  prueba Postman que lo cubre.
- [`docs/guion_kaltura.md`](docs/guion_kaltura.md): guion de la
  demostración de 7 a 10 minutos.
