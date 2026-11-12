package mx.tvmaze;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("show_comments")
record ShowComment(@Id String id, @Indexed long showId, String comment, int rating) {
}
