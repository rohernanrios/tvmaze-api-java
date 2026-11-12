package mx.tvmaze;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

@Service
class ShowService {
    private final TvMazeClient tvMaze;
    private final ShowCacheRepository cache;
    private final ObjectMapper mapper;

    ShowService(TvMazeClient tvMaze, ShowCacheRepository cache, ObjectMapper mapper) {
        this.tvMaze = tvMaze;
        this.cache = cache;
        this.mapper = mapper;
    }

    JsonNode show(long id) {
        var cached = cache.findById(id);
        if (cached.isPresent()) {
            try {
                return mapper.readTree(cached.get().json());
            } catch (JsonProcessingException exception) {
                throw new IllegalStateException("El show almacenado contiene JSON inválido", exception);
            }
        }
        JsonNode show = tvMaze.show(id);
        cache.save(new CachedShow(id, show.toString()));
        return show;
    }
}
