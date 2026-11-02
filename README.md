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
                └────────────► Oracle Database Free / FREEPDB1 (esquema FORO_APP) ◄───┘
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
Database Free (contenedor local, PDB `FREEPDB1`), Postman.

Estas son las mismas versiones del ejemplo del profesor. **Se comprobó
realmente** (no solo por inspección) que `spring-boot-starter-parent:4.1.1`
y `spring-boot-starter-webmvc` existen y son compatibles con Java 21:
se compiló el proyecto `dp-usuarios` del ejemplo con Maven y JDK 21 en este
equipo, y terminó con éxito (`mvn compile`, `BUILD SUCCESS`). No fue
necesario cambiar ninguna versión.

Además, **ambos microservicios se ejecutaron realmente** contra una base
Oracle Database Free local (Docker, Apple Silicon) y se probaron con
peticiones HTTP reales (sección 10): esto permitió detectar y corregir dos
errores que la sola revisión de código no mostró — ver sección 10.1.

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

Este proyecto se probó realmente usando **Oracle Database Free** (no XE)
en un contenedor Docker local en un Mac Apple Silicon (arm64), con la
imagen oficial `gvenzl/oracle-free:23-slim`. Es la opción recomendada:
simple, oficial, compatible con ARM64, y es exactamente lo que se usó
para todas las pruebas de la sección 10.

**Importante:** el nombre real del servicio del PDB es **`FREEPDB1`**, no
`XEPDB1` (eso corresponde a Oracle XE, una distribución distinta). Antes
de asumir un nombre de servicio, verifícalo con
`SELECT name, open_mode FROM v$pdbs;`.

### 5.1 Levantar Oracle con Docker (ruta recomendada)

No se usa Docker Compose (solo `docker run`), no se reemplaza Oracle por
otra base, y los microservicios **no** corren en contenedores — solo
Oracle. El contenedor usa un volumen propio (`fs3-oracle-data`) y un
puerto propio (**1522**, no el 1521 por defecto) publicado únicamente en
`127.0.0.1`, precisamente para no interferir con ningún otro contenedor
Oracle que ya tengas en Docker para otros proyectos (si tienes uno en el
puerto 1521, sigue intacto y puede usarse en paralelo).

```bash
# Solo la primera vez: crea el volumen y el contenedor.
docker volume create fs3-oracle-data

docker run -d \
  --name fs3-oracle \
  -p 127.0.0.1:1522:1521 \
  -e ORACLE_PASSWORD="elige_una_clave_para_SYS_SYSTEM" \
  -e APP_USER=FORO_APP \
  -e APP_USER_PASSWORD="elige_una_clave_para_FORO_APP" \
  -v fs3-oracle-data:/opt/oracle/oradata \
  gvenzl/oracle-free:23-slim
```

`APP_USER`/`APP_USER_PASSWORD` hacen que la imagen cree automáticamente el
usuario/esquema `FORO_APP` (con el rol `DB_DEVELOPER_ROLE`: privilegios
suficientes para crear sus propias tablas, sin ser administrador de la
base) — por eso **no es necesario ejecutar `sql/00_crear_esquema.sql`**
con este método; ese script queda solo como referencia para una
instalación tradicional de Oracle (ver sección 5.3).

La primera vez, Oracle tarda varios minutos en inicializar la base.
Sigue el progreso con:

```bash
docker logs -f fs3-oracle
# Esperar la linea: "DATABASE IS READY TO USE!"
```

### 5.2 Crear las tablas (una sola vez)

Como no hay cliente Oracle instalado en el Mac (`sqlplus` no está
disponible fuera del contenedor), se ejecutan los scripts usando el
`sqlplus` que ya viene dentro de la imagen:

```bash
docker cp sql/01_crear_tablas_usuarios.sql fs3-oracle:/tmp/01.sql
docker cp sql/02_crear_tablas_foro.sql      fs3-oracle:/tmp/02.sql

docker exec fs3-oracle bash -c '
sqlplus -s FORO_APP/"TU_CLAVE_DE_FORO_APP"@localhost:1521/FREEPDB1 <<EOF
@/tmp/01.sql
@/tmp/02.sql
SELECT table_name FROM user_tables ORDER BY table_name;
EOF
'
```

Dentro del contenedor, Oracle siempre escucha en su puerto interno 1521
(el 1522 es solo el puerto publicado hacia el Mac); por eso el `sqlplus`
de dentro del contenedor usa `localhost:1521`, mientras que los
microservicios (fuera del contenedor) usan `localhost:1522`.

**No ejecutar todavía** `sql/03_datos_iniciales_foro.sql`: necesita que
`ms-usuarios` haya creado antes las 3 cuentas iniciales (sección 7).

`spring.jpa.hibernate.ddl-auto=validate` en ambos microservicios: Hibernate
nunca crea ni altera tablas, solo valida que coincidan con las entidades.

### 5.3 Alternativa: Oracle XE / una instalación tradicional

Si en vez del contenedor usas una instalación de Oracle XE ya existente,
usa `sql/00_crear_esquema.sql` para crear el usuario `FORO_APP` a mano
(conectado como `SYSTEM`/`SYS`), y ajusta `DB_URL` (sección 6) al host,
puerto y nombre de servicio reales de esa instalación — nunca asumas que
es `XEPDB1` ni `FREEPDB1` sin comprobarlo primero.

## 6. Variables de entorno

Ningún secreto se guarda en el código ni en los `application.properties`
(solo referencias `${VARIABLE}`). Cada proyecto tiene un `.env.example`
ilustrativo (sin valores reales, sí versionado). Para no repetir `export`
en cada terminal nueva, se puede crear un `.env.local` propio en cada
proyecto (no se versiona: `.gitignore` tiene `.env*` excepto
`.env.example`) y cargarlo antes de arrancar:

```bash
# ms-usuarios/.env.local (crear a mano, no se versiona)
DB_USER=FORO_APP
DB_PASSWORD=la_clave_de_FORO_APP_del_paso_5.1
FORO_ADMIN_PASS=clave_admin_demo_8_o_mas_caracteres
FORO_USUARIO1_PASS=clave_usuario1_demo
FORO_USUARIO2_PASS=clave_usuario2_demo

# ms-foro/.env.local (crear a mano, no se versiona)
DB_USER=FORO_APP
DB_PASSWORD=la_clave_de_FORO_APP_del_paso_5.1
FORO_USUARIOS_URL=http://localhost:8081
```

```bash
cd ms-usuarios && set -a && source .env.local && set +a && ./mvnw spring-boot:run
```

Si `FORO_ADMIN_PASS`/`FORO_USUARIO1_PASS`/`FORO_USUARIO2_PASS` no están
definidas, `ms-usuarios` arranca igual pero **no crea ninguna cuenta** y
deja un `WARN` explícito en el log indicando exactamente qué variables
faltan (no queda "a medias" en silencio).

`DB_URL` es opcional en ambos microservicios: si no se define, apunta por
defecto al contenedor Docker de la sección 5.1
(`jdbc:oracle:thin:@//localhost:1522/FREEPDB1`). Solo defínela si usas
otro host/puerto/servicio (por ejemplo, la alternativa de la sección 5.3).

## 7. Orden de ejecución (verificado en este equipo)

1. **Oracle arriba** (sección 5.1) y **tablas creadas** (sección 5.2).
2. **Compilar** ambos proyectos:
   ```bash
   cd ms-usuarios && ./mvnw -q compile && cd ..
   cd ms-foro      && ./mvnw -q compile && cd ..
   ```
3. **Arrancar `ms-usuarios`** (con `.env.local` de la sección 6):
   ```bash
   cd ms-usuarios && set -a && source .env.local && set +a && ./mvnw spring-boot:run
   ```
   En el primer arranque, con la tabla `USUARIOS` vacía, crea
   automáticamente 3 cuentas: `admin` (ADMIN), `usuario1` y `usuario2`
   (USUARIO), con las contraseñas de las variables de entorno, ya
   hasheadas con BCrypt. En arranques siguientes detecta que ya existen
   cuentas y no vuelve a insertarlas (no se duplican datos al reiniciar;
   verificado reiniciando el proceso — log: "Ya existen cuentas en
   USUARIOS: no se ejecuta la carga inicial").
4. Con `ms-usuarios` arriba y las 3 cuentas creadas, ejecutar
   `sql/03_datos_iniciales_foro.sql` (conectado como `FORO_APP`, por
   ejemplo con el mismo patrón `docker cp` + `docker exec sqlplus` de la
   sección 5.2): carga 6 categorías, 5 temas y 6 comentarios ficticios,
   usando subconsultas por `USERNAME`/`NOMBRE`/`TITULO` para no asumir
   IDs fijos y sin duplicar si se ejecuta más de una vez por error.
5. **Arrancar `ms-foro`** en otra terminal (con su propio `.env.local`):
   ```bash
   cd ms-foro && set -a && source .env.local && set +a && ./mvnw spring-boot:run
   ```
6. Probar con Postman (sección 8) y verificar en SQL Developer (sección
   11) con `sql/04_verificar.sql`.

### Detener todo (sin perder datos)

```bash
# Detener ms-usuarios y ms-foro: Ctrl+C en cada terminal, o:
kill $(lsof -nP -iTCP:8081 -sTCP:LISTEN -t)
kill $(lsof -nP -iTCP:8080 -sTCP:LISTEN -t)

# Detener Oracle (conserva el volumen fs3-oracle-data: los datos quedan intactos)
docker stop fs3-oracle

# Para volver a arrancar Oracle mas tarde (mismo contenedor, mismos datos):
docker start fs3-oracle
```

**Nunca** ejecutar `docker rm -v fs3-oracle` ni `docker volume rm
fs3-oracle-data` a menos que quieras borrar la base por completo.

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

**Cómo empezar en la app de Postman:**
1. Abrir Postman → File → Import → arrastrar `postman/Foro_Collection.json`
   y `postman/Foro_Entorno.json`.
2. Arriba a la derecha, seleccionar el entorno **"Foro en Línea - Local"**.
3. Click en el ícono de ojo junto al entorno → Edit → completar
   `adminPass`, `usuario1Pass`, `usuario2Pass` y `usuarioPruebaPass` con
   las contraseñas reales que definiste en tu `.env.local` de
   `ms-usuarios` (sección 6) → Save.
4. Con Oracle y ambos microservicios arriba (secciones 5 y 7), abrir la
   carpeta `00` y ejecutar sus requests una por una (botón Send), y luego
   seguir con `01`, `02`, `03`, `04`, `05`, `06` en orden. Cada request
   tiene una pestaña "Test Results" que marca en verde si el código HTTP
   obtenido coincide con el esperado.
5. También puede ejecutarse automáticamente con el botón "Run" sobre el
   nombre de la colección (Postman Runner), que corre todas las carpetas
   en orden y muestra un resumen pasa/falla al final.

No se ejecutó la colección dentro de la app de Postman en este equipo,
pero sí se verificó, con peticiones HTTP reales equivalentes (`curl`) a
cada request de la colección, que el backend responde exactamente lo que
cada carpeta espera — ver sección 10.

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
  construir un sistema de sincronización entre microservicios. (Verificado
  en Oracle real — ver sección 10.)
- **Autor real, nunca arbitrario**: al crear un tema o comentario, el autor
  se obtiene desde la identidad autenticada (`ms-usuarios` vía
  `UsuariosRemotosProvider`), no desde el JSON de la petición.
- **Modificar/eliminar solo contenido propio**: validado comparando el
  autor guardado con la identidad autenticada; si no coincide, `403`. No
  existe ninguna ruta de moderación ni de "admin edita cualquier post".

## 10. Qué se verificó realmente vs. qué queda pendiente

**Verificado en este equipo, de extremo a extremo, con Oracle real
(Docker, sección 5.1) y ambos microservicios realmente corriendo — no
solo compilado ni revisado estáticamente:**

- `ms-usuarios` y `ms-foro` compilan (`./mvnw compile`, `BUILD SUCCESS`) y
  **arrancan** y se conectan a Oracle (logs: `HikariPool-1 - Added
  connection oracle.jdbc.driver.T4CConnection...`, y `ddl-auto=validate`
  pasó sin error, confirmando que las tablas reales coinciden con las
  entidades JPA).
- Las 3 cuentas iniciales se crean solas en el primer arranque de
  `ms-usuarios` (`admin` id=1, `usuario1` id=2, `usuario2` id=3) y **no**
  se duplican en un segundo arranque.
- `sql/03_datos_iniciales_foro.sql` cargó 6 categorías, 5 temas y 6
  comentarios reales en Oracle.
- Se ejecutaron **39 peticiones HTTP reales** (equivalentes a cada request
  de la colección Postman) contra los dos servicios ya arriba, cubriendo
  exactamente los casos pedidos — **39 de 39 pasaron**:
  - Login correcto (200) e incorrecto (401, sin credenciales y con clave
    incorrecta).
  - Permisos ADMIN vs. USUARIO (`GET /api/usuarios`: 200 para ADMIN, 403
    para USUARIO).
  - CRUD de usuarios completo: crear (201, sin password/hash en la
    respuesta), duplicado (409), consultar (200), modificar (200), datos
    inválidos (400), eliminar (204), consultar tras eliminar (404),
    proteger al último ADMIN (409).
  - Consulta de categorías (200, 6 categorías ≥ 5 exigidas).
  - Temas: crear (201, autor tomado de la identidad autenticada),
    consultar detalle con sus comentarios (200), buscar por texto (200),
    filtrar por categoría (200), modificar propio (200), eliminar propio
    (204), consultar tras eliminar (404), categoría inexistente (404),
    título vacío (400).
  - Comentarios: crear (201), consultar (200), modificar propio (200),
    eliminar propio (204).
  - Rechazo al modificar/eliminar contenido ajeno: tema ajeno (403),
    comentario ajeno (403).
  - Recursos inexistentes: tema (404), categoría (404).
- **Cascada real verificada en Oracle**: se creó un tema con un
  comentario sin borrarlo antes, se confirmó con una consulta SQL que
  ambas filas existían, se eliminó el tema por la API, y una nueva
  consulta SQL confirmó que el comentario también desapareció
  (`ON DELETE CASCADE`).
- **Conservación de publicaciones verificada en Oracle**: se creó un
  usuario de prueba, publicó un tema, se eliminó esa cuenta en
  `ms-usuarios`, y el tema siguió existiendo en `ms-foro` con el
  `autorUsername` guardado — tal como describe la sección 9.
- **Persistencia tras reinicio verificada**: se reiniciaron el contenedor
  Oracle (`docker restart fs3-oracle`, sin tocar el volumen) y ambos
  microservicios; se confirmó por SQL y por HTTP que los 3 usuarios, 6
  categorías, 5 temas y 6 comentarios seguían exactamente igual, y que
  `ms-usuarios` no volvió a crear cuentas.
- Los JSON de Postman (`Foro_Collection.json`, `Foro_Entorno.json`) son
  JSON válido.

### 10.1 Dos errores reales encontrados y corregidos durante estas pruebas

Estos **no** aparecían con una simple revisión del código; solo se vieron
al ejecutar de verdad:

1. **Regla de seguridad ambigua en `ms-foro`** (`SeguridadConfig.java`):
   la primera versión declaraba una regla "GET público" y, por separado,
   otra regla "resto autenticado" apuntando a los mismos patrones de ruta
   (`/api/temas`, `/api/temas/**`). Al ejecutar peticiones reales, el GET
   a rutas con parámetros (`/api/temas/1`, `/api/temas/1/comentarios`,
   etc.) terminaba devolviendo 401 en vez de 200, porque la petición
   pasaba el control de seguridad pero el controlador fallaba luego por
   el error #2 y el reenvío interno a `/error` sí exigía autenticación.
   **Corrección:** se simplificó a una sola regla "GET público" por
   recurso y un único `.anyRequest().authenticated()` final, sin reglas
   repetidas sobre el mismo patrón de ruta.
2. **`java.time.Instant` incompatible con la columna Oracle `TIMESTAMP`**
   (`Tema.java`, `Comentario.java`): al leer `FECHA_CREACION` de vuelta
   desde Oracle, Hibernate lanzaba `ORA-18716 ({0} no está en ninguna
   zona horaria)`. Las columnas `TIMESTAMP` de Oracle no llevan zona
   horaria, y `Instant` sí la requiere. **Corrección:** se cambió el tipo
   del campo a `java.time.LocalDateTime` (fecha+hora sin zona, que es
   exactamente lo que `TIMESTAMP` representa) en las entidades `Tema` y
   `Comentario`, sus DTO de respuesta y los `Service` que las crean; no
   fue necesario cambiar la tabla ni ningún script SQL.

Ambas correcciones ya están aplicadas en el código entregado y vueltas a
probar (39/39 pasaron después del arreglo).

### 10.2 Lo que sigue pendiente (no se declaró aprobado sin probarlo)

- **Ejecutar la colección dentro de la app de Postman** (se probó el
  mismo conjunto de peticiones con `curl`, pero no se abrió Postman en
  este equipo — sección 8 explica cómo hacerlo).
- **Grabación de la demostración en Kaltura** (ver `docs/guion_kaltura.md`):
  es una acción manual que debes hacer tú.
- Las pruebas se corrieron todas en una sola sesión de trabajo; no se
  dejó Oracle ni los microservicios corriendo de forma permanente — usa
  la sección 7 para volver a levantarlos cuando los necesites.

## 11. SQL Developer: cómo conectarte

Datos de conexión al contenedor `fs3-oracle` (los mismos que usan los
microservicios):

| Campo | Valor |
|---|---|
| Hostname | `localhost` |
| Puerto | `1522` |
| Tipo de conexión | Basic — **Service name** (no SID) |
| Service name | `FREEPDB1` |
| Username | `FORO_APP` |
| Password | la que definiste en `APP_USER_PASSWORD` al crear el contenedor (sección 5.1) — no se publica aquí |
| Role | default |

Si además quieres conectarte como administrador (por ejemplo para ver
`v$pdbs` o crear otro usuario): username `SYS`, mismo host/puerto/service
name, password = `ORACLE_PASSWORD` del `docker run`, **Role: SYSDBA**.

## 12. Documentos adicionales

- [`docs/matriz_trazabilidad.md`](docs/matriz_trazabilidad.md): cada
  requisito oficial, su implementación concreta (archivo/endpoint) y la
  prueba Postman que lo cubre.
- [`docs/guion_kaltura.md`](docs/guion_kaltura.md): guion de la
  demostración de 7 a 10 minutos.
