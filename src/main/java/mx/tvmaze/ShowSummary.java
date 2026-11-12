package mx.tvmaze;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.ArrayList;
import java.util.List;

record ShowSummary(long id, String name, String channel, String summary, List<String> genres) {
    static ShowSummary from(JsonNode show) {
        List<String> genres = new ArrayList<>();
        show.path("genres").forEach(genre -> genres.add(genre.asText()));
        String webChannel = show.path("webChannel").path("name").asText(null);
        return new ShowSummary(
                show.path("id").asLong(),
                show.path("name").asText(null),
                show.path("network").path("name").asText(webChannel),
                show.path("summary").asText(null),
                genres
        );
    }
}
