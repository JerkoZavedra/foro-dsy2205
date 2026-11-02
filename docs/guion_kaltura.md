# Guion de demostración — Kaltura (7 a 10 minutos)

Grabar en pantalla completa mostrando: terminal, Postman y SQL Developer.
Antes de grabar, dejar listo: Oracle corriendo con las tablas y datos
cargados (README §5–7), las dos terminales con las variables de entorno
exportadas, y el entorno de Postman con las contraseñas completadas (nunca
mostrar las contraseñas en pantalla al escribirlas; completarlas antes de
empezar a grabar).

## 0. Introducción (0:00 – 0:40)

- Mostrar la cara (opcional) o solo pantalla, y presentarse: nombre,
  evaluación (DSY2205, Experiencia 1, Semana 2), caso asignado (sistema de
  foros en línea).
- Enunciar brevemente qué se va a mostrar: dos microservicios Spring Boot
  independientes (`ms-usuarios` 8081, `ms-foro` 8080), Oracle local, y
  pruebas con Postman.

## 1. Arranque de los microservicios (0:40 – 2:00)

- Terminal 1: mostrar las variables de entorno ya exportadas (sin revelar
  contraseñas: usar `echo $DB_USER` pero no `echo $DB_PASSWORD`), ejecutar
  `./mvnw spring-boot:run` dentro de `ms-usuarios`. Esperar el log
  `Started UsuariosApplication`.
- Terminal 2: mismo procedimiento para `ms-foro` (`Started ForoApplication`).
- Mostrar brevemente SQL Developer con `sql/04_verificar.sql` ya ejecutado:
  3 usuarios, 6 categorías, 5 temas, 6 comentarios ya existentes.

## 2. Autenticación y permisos (2:00 – 3:30)

En Postman, carpeta `01 · Autenticacion y permisos`:

- `GET /api/auth/perfil` **sin credenciales** → 401. Explicar: HTTP Basic,
  sin token, sin sesión.
- `GET /api/auth/perfil` con `admin` (credenciales correctas) → 200, mostrar
  que devuelve `id`, `username`, `rol`.
- Repetir con una contraseña incorrecta → 401.
- `GET /api/usuarios` como `admin` → 200 (lista las 3 cuentas).
- `GET /api/usuarios` como `usuario1` → 403. Explicar: solo ADMIN administra
  cuentas.

## 3. CRUD de usuarios (3:30 – 5:00)

Carpeta `02 · CRUD de usuarios`:

- `POST /api/usuarios` (ADMIN) crea una cuenta de prueba → 201. Señalar que
  la respuesta **no** incluye la contraseña ni su hash.
- Repetir el mismo `POST` → 409 (username duplicado).
- `GET /api/usuarios/{id}` → 200.
- `PUT /api/usuarios/{id}` → 200 (cambia el correo).
- `DELETE /api/usuarios/1` (el ADMIN semilla) → 409, explicar la protección
  del último administrador.
- `DELETE /api/usuarios/{id}` de la cuenta de prueba → 204.
- `GET /api/usuarios/{id}` de esa misma cuenta → 404.

## 4. Foro: categorías, búsqueda y detalle (5:00 – 6:15)

Carpeta `03 · Categorias, busqueda y detalle de temas`:

- `GET /api/categorias` (sin login) → 200, contar que hay 6 (mínimo 5).
- `GET /api/temas` → 200, lista de temas.
- `GET /api/temas?categoriaId=1` → 200, filtrado.
- `GET /api/temas?texto=lenguaje` → 200, búsqueda por texto.
- `GET /api/temas/{id}` → 200, mostrar que el detalle incluye sus
  comentarios.

## 5. Publicar, comentar y proteger contenido propio (6:15 – 8:30)

Carpeta `04 · Publicar, comentar y editar contenido propio`:

- `POST /api/temas` sin login → 401.
- `POST /api/temas` autenticado como `usuario1` → 201, señalar que el autor
  (`autorId`, `autorUsername`) viene de la identidad autenticada, no del
  JSON enviado.
- `POST /api/temas` como `usuario2` (tema ajeno, para la prueba siguiente).
- `PUT` del tema propio (`usuario1`) → 200.
- `PUT` del tema de `usuario2` intentado por `usuario1` → 403. Explicar:
  solo el autor modifica o elimina su contenido; no hay moderación de
  ADMIN sobre contenido ajeno.
- `POST /api/temas/{id}/comentarios` (comentar) → 201.
- `PUT`/`DELETE` de ese comentario, y el intento de editarlo con otro
  usuario → 403.
- `DELETE` del tema propio → 204; a continuación `GET` del mismo tema →
  404, y mostrar en SQL Developer (`sql/04_verificar.sql`) que sus
  comentarios desaparecieron también (regla de eliminación en cascada).

## 6. Datos inválidos y verificación en Oracle (8:30 – 9:30)

Carpeta `05 · Datos invalidos, duplicados y recursos inexistentes`:

- Un par de ejemplos rápidos: contraseña demasiado corta (400), categoría
  inexistente al crear un tema (404).
- Cambiar a SQL Developer y ejecutar `sql/04_verificar.sql`: mostrar que
  las categorías, temas y comentarios iniciales siguen intactos (solo se
  crearon/eliminaron registros de prueba), **sin mostrar `PASS_HASH`**.

## 7. Persistencia y cierre (9:30 – 10:00)

- Detener ambos procesos (`Ctrl+C`), volver a ejecutar `./mvnw spring-boot:run`
  en los dos, y repetir un `GET /api/temas` y `GET /api/usuarios` para
  mostrar que los datos persisten tras el reinicio.
- Cierre: resumir que se cumplieron los requisitos del caso (2 roles, 2
  microservicios, Oracle, Postman, CRUD completo) y agradecer.

**Duración total objetivo: 9 a 10 minutos.** Si se excede, recortar la
sección 6 (datos inválidos) a un solo ejemplo.
