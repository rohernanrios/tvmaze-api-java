# TVMaze API

Middleware en Java 21 y Spring Boot para consultar TVMaze y guardar comentarios en MongoDB.

## Ejecutar

Se requiere Java 21, Maven 3.9 y una instancia de MongoDB. Para iniciar MongoDB en local con Docker y levantar la aplicación:

```bash
docker compose up -d
mvn spring-boot:run
```

La API escucha en `http://localhost:8080`. La conexión local predeterminada usa la base de datos `tvmaze`.

### MongoDB Atlas

1. Crea un clúster gratuito y un usuario de base de datos con permisos de lectura y escritura sobre `tvmaze`.
2. Agrega `0.0.0.0/0` a la lista de acceso del proyecto según el requisito de la evaluación.
3. Copia la URI de conexión de Atlas, completa los datos del usuario y agrega `/tvmaze` como nombre de la base de datos. Configura la URI en la variable de entorno `MONGODB_URI` antes de iniciar la aplicación. Conserva las credenciales fuera del repositorio.

[Documentación de conexión de Atlas](https://www.mongodb.com/docs/atlas/connect-to-database-deployment/)

## Endpoints

| Método | Ruta | Resultado |
| --- | --- | --- |
| GET | `/search?search_query=breaking%20bad` | Lista con `id`, `name`, `channel`, `summary`, `genres` y `comments` |
| GET | `/show/169` | Objeto completo de TVMaze con `comments` |
| POST | `/comments` | HTTP 201 y `{"status":"created"}` |

```bash
curl --get 'http://localhost:8080/search' --data-urlencode 'search_query=breaking bad'

curl 'http://localhost:8080/show/169'

curl -X POST 'http://localhost:8080/comments' \
  -H 'Content-Type: application/json' \
  -d '{"show_id":169,"comment":"Muy buena serie","rating":5}'
```

La búsqueda conserva el orden de TVMaze. `channel` usa el nombre de la cadena o del canal web. El detalle consulta primero `show_cache` por ID y guarda el JSON original al encontrar un show nuevo. Los comentarios se consultan desde `show_comments` en cada solicitud y se devuelven como objetos con `comment` y `rating`. Un show sin comentarios devuelve `[]`.

`rating` acepta enteros de 0 a 5. Un cuerpo inválido devuelve HTTP 400. Si el show no existe, el detalle y el registro de comentarios devuelven HTTP 404. Si TVMaze falla o no responde, la API devuelve HTTP 502.

## Pruebas

```bash
mvn test
```
