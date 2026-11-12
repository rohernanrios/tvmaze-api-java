package mx.tvmaze;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("show_cache")
record CachedShow(@Id Long id, String json) {
}
