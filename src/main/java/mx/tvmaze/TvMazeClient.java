package mx.tvmaze;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.server.ResponseStatusException;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;

@Component
class TvMazeClient {
    private final RestClient client;

    TvMazeClient(RestClient.Builder builder, @Value("${tvmaze.base-url}") String baseUrl) {
        var factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(Duration.ofSeconds(3));
        factory.setReadTimeout(Duration.ofSeconds(8));
        client = builder.baseUrl(baseUrl).requestFactory(factory).build();
    }

    List<JsonNode> search(String query) {
        JsonNode[] results = get("/search/shows?q={query}", JsonNode[].class, query);
        return Arrays.stream(results).map(result -> result.get("show")).toList();
    }

    private <T> T get(String path, Class<T> type, Object parameter) {
        try {
            T body = client.get().uri(path, parameter).retrieve().body(type);
            if (body == null) {
                throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Respuesta vacía de TVMaze");
            }
            return body;
        } catch (RestClientResponseException exception) {
            if (exception.getStatusCode() == HttpStatus.NOT_FOUND) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Show no encontrado");
            }
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Error de TVMaze");
        } catch (RestClientException exception) {
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "TVMaze no está disponible");
        }
    }
}
