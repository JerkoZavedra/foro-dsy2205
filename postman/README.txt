1. Importar Foro_Collection.json y Foro_Entorno.json en Postman.
2. Seleccionar el entorno "Foro en Linea - Local".
3. Completar adminPass, usuario1Pass, usuario2Pass y usuarioPruebaPass con las
   claves ficticias que se definieron en la terminal al arrancar ms-usuarios
   (ver README raiz); nunca compartir estos valores.
4. Arrancar ms-usuarios (8081) antes que ms-foro (8080).
5. Ejecutar las carpetas 00 a 06 en orden.
6. Los IDs de usuario de prueba, tema y comentario se guardan automaticamente
   en el entorno a partir de las respuestas POST: no se asumen IDs fijos,
   excepto el ADMIN semilla (id 1), usado solo para comprobar que no se puede
   eliminar al ultimo administrador.
7. Las carpetas 02 y 04 crean y eliminan sus propios registros temporales;
   las categorias, temas y comentarios iniciales (sql/03_datos_iniciales_foro.sql)
   no se modifican ni se eliminan al correr la coleccion.
8. Verificar los cambios desde SQL Developer usando sql/04_verificar.sql.
La configuracion de HTTP Basic es exclusivamente didactica en localhost.
