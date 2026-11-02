# Matriz de trazabilidad — requisito oficial → implementación → prueba

Fuentes: `DSY2205_Exp1_S2_Instrucciones_especificas_B.docx` (documento
oficial) y las indicaciones adicionales del estudiante para acotar el caso
(estas últimas marcadas **[Indicación]**).

| # | Requisito | Implementación | Prueba |
|---|---|---|---|
| 1 | Framework Spring Boot | `ms-usuarios/pom.xml`, `ms-foro/pom.xml` (`spring-boot-starter-parent:4.1.1`) | `./mvnw compile` → `BUILD SUCCESS` en ambos (verificado) |
| 2 | Organización con Git | Repositorio Git local en la raíz del proyecto (`FS3/`) | `git log` / `git status` |
| 3 | Conexión a Oracle con tablas propias | `sql/00_crear_esquema.sql` a `02_crear_tablas_foro.sql`; `application.properties` de cada microservicio (`spring.datasource.*`, `ddl-auto=validate`) | `sql/04_verificar.sql`; Postman carpeta `00` (`GET /api/estado`) |
| 4 | Controladores RESTful GET/POST/PUT/DELETE | `UsuarioController`, `AuthController` (ms-usuarios); `CategoriaController`, `TemaController`, `ComentarioController` (ms-foro) | Postman carpetas `01` a `05` |
| 5 | Al menos 3 registros por tabla | `CuentasIniciales` (3 cuentas: 1 ADMIN + 2 USUARIO); `sql/03_datos_iniciales_foro.sql` (6 categorías, 5 temas, 6 comentarios) | `sql/04_verificar.sql`; Postman carpeta `06` |
| 6 | Uso de Postman para validar | `postman/Foro_Collection.json` + `Foro_Entorno.json` | Ejecutar la colección completa en Postman |
| 7 | Caso "Sistema de Foros en Línea" con 5+ categorías | `Categoria` (modelo), `CategoriaController`, seed de 6 categorías | Postman `03 · GET categorias (minimo 5)` |
| 8 | Al menos 2 roles con privilegios distintos | `Rol.ADMIN` / `Rol.USUARIO` (ms-usuarios); `SeguridadConfig` de ambos microservicios | Postman `01 · GET /api/usuarios como ADMIN (200)` vs `como USUARIO (403)` |
| 9 | No implementar sistemas de pago | No existe ningún endpoint, entidad ni dependencia relacionada con pagos | Revisión del código (no aplica prueba funcional) |
| 10 | Desarrollar 2 de los 3 microservicios, sin frontend | `ms-usuarios` (crear/consultar/modificar/eliminar/login) y `ms-foro` (buscar/ver/publicar/comentar); no se desarrolla el tercero (moderación) ni frontend | Estructura de carpetas `ms-usuarios/`, `ms-foro/`; ausencia de carpeta de frontend |
| 11 **[Indicación]** | Roles ADMIN/USUARIO (reemplaza CLIENTE) | `cl.duoc.usuarios.model.Rol` | Postman `01` |
| 12 **[Indicación]** | Solo ADMIN crea/consulta/modifica/elimina usuarios; ambos roles se autentican | `SeguridadConfig` (ms-usuarios): `/api/usuarios/**` → `hasRole("ADMIN")`; `/api/auth/**` → `authenticated()` | Postman `01`, `02` |
| 13 **[Indicación]** | Sin registro público | No existe endpoint `POST` sin autenticación para crear usuarios | Postman `04 · POST crear tema SIN login (401)` (mismo patrón aplicado a `/api/usuarios`, protegido con `hasRole("ADMIN")`) |
| 14 **[Indicación]** | Contraseñas con BCrypt, nunca devueltas | `BCryptPasswordEncoder` en `SeguridadConfig`; `UsuarioResponse` no incluye `password` ni `passHash` | Postman `02 · POST crear usuario` (test: `password`/`passHash` `undefined` en la respuesta) |
| 15 **[Indicación]** | Username y correo sin duplicados | `UsuarioRepository.existsByUsername/existsByEmail`; `UsuarioService.crear/actualizar` | Postman `02 · POST crear usuario duplicado (409)` |
| 16 **[Indicación]** | Protección del último ADMIN | `UsuarioService.actualizar/eliminar` (`countByRol(ADMIN) <= 1`) | Postman `02 · DELETE ultimo ADMIN (409, protegido)` |
| 17 **[Indicación]** | Perfil autenticado devuelve id, username y rol | `PerfilResponse(Long id, String username, String rol)`; `AuthController.perfil` | Postman `01 · GET perfil admin` |
| 18 **[Indicación]** | Login HTTP Basic explicado (sin token ni sesión) | `SeguridadConfig` (`SessionCreationPolicy.STATELESS`, `httpBasic`); explicado en README §7 | Postman `01` (cada request reenvía credenciales) |
| 19 **[Indicación]** | Consultar categorías | `GET /api/categorias`, `GET /api/categorias/{id}` | Postman `03` |
| 20 **[Indicación]** | Listar y ver detalle de temas | `GET /api/temas`, `GET /api/temas/{id}` (incluye comentarios) | Postman `03` |
| 21 **[Indicación]** | Buscar por texto y filtrar por categoría (simple) | `TemaRepository.buscar` (JPQL con filtros opcionales); `GET /api/temas?categoriaId=&texto=` | Postman `03 · filtrar por categoria` / `buscar por texto` |
| 22 **[Indicación]** | USUARIO y ADMIN pueden publicar temas y comentar | `SeguridadConfig` (ms-foro): escritura en `/api/temas/**` y `/api/comentarios/**` → `authenticated()` (sin restringir por rol) | Postman `04 · POST crear tema (usuario1)` |
| 23 **[Indicación]** | Modificar/eliminar solo contenido propio | `TemaService.exigirPropietario`, `ComentarioService` (comparan `autorId` vs. identidad autenticada) | Postman `04 · PUT intentar modificar tema ajeno (403)` / comentario ajeno |
| 24 **[Indicación]** | Autor desde la identidad autenticada, nunca arbitrario | `@AuthenticationPrincipal UsuarioAutenticado` en `TemaController`/`ComentarioController`; `TemaRequest`/`ComentarioRequest` no tienen campo autor | Revisión de código: el DTO de entrada no acepta autor |
| 25 **[Indicación]** | Sin baneo ni moderación administrativa | No existe ningún endpoint ni regla que permita a ADMIN alterar contenido de otro usuario | Postman `04` (ADMIN no tiene un camino especial: mismas reglas de propiedad) |
| 26 **[Indicación]** | GET/POST/PUT/DELETE donde corresponda | Ver tabla de endpoints en README y controladores de ambos microservicios | Postman `01` a `05` |
| 27 **[Indicación]** | Foro no consulta la tabla de usuarios para autenticar | `UsuariosRemotosProvider` (ms-foro) llama a `ms-usuarios` por HTTP; `ms-foro` no tiene entidad `Usuario` ni repositorio sobre esa tabla | Revisión de código; `sql/02_crear_tablas_foro.sql` (sin FK a `USUARIOS`) |
| 28 **[Indicación]** | Un solo Oracle local, un esquema, tablas separadas | `sql/00_crear_esquema.sql` (usuario `FORO_APP` único) | `sql/04_verificar.sql` |
| 29 **[Indicación]** | Regla explícita: eliminar tema con comentarios | `ON DELETE CASCADE` en `COMENTARIOS.TEMA_ID` (`sql/02_crear_tablas_foro.sql`) | Postman `04 · DELETE tema propio ... (cascada de comentarios)` |
| 30 **[Indicación]** | Regla explícita: eliminar usuario con publicaciones | Sin FK de `TEMAS`/`COMENTARIOS` a `USUARIOS`; se conserva `AUTOR_USERNAME` | Documentado en README §9 (requiere Oracle para probarlo end-to-end) |
| 31 **[Indicación]** | Datos iniciales ficticios, sin duplicar ni quedar incompletos | `CuentasIniciales` (chequea `repo.count()>0`); `sql/03_datos_iniciales_foro.sql` (`WHERE NOT EXISTS`) | `sql/04_verificar.sql` (contar filas tras ejecutar dos veces) |
| 32 **[Indicación]** | Sin hashes BCrypt inventados en el código/SQL | `sql/01_crear_tablas_usuarios.sql` no inserta filas; las contraseñas viajan solo por variables de entorno | Revisión de código y de los scripts SQL |
| 33 | Entregable: Postman sin credenciales reales | `Foro_Entorno.json` con todos los `*Pass` vacíos (`type: secret`) | Inspección del archivo |
| 34 | Entregable: README con requisitos, Oracle, variables, orden, pruebas | `README.md` | Lectura del documento |
| 35 | Entregable: `.gitignore` apropiado | `.gitignore` por microservicio (ignora `target/`, `.env*`) | Inspección del archivo |
| 36 | Entregable: guion Kaltura 7–10 min | `docs/guion_kaltura.md` | Grabación pendiente (ver README §10) |
| 37 | Entregable: esta matriz | `docs/matriz_trazabilidad.md` | — |
