package mx.tvmaze;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
class CommentController {
    private final ShowCommentRepository comments;
    private final ShowService shows;

    CommentController(ShowCommentRepository comments, ShowService shows) {
        this.comments = comments;
        this.shows = shows;
    }

    @PostMapping("/comments")
    @ResponseStatus(HttpStatus.CREATED)
    Map<String, String> create(@Valid @RequestBody CommentRequest request) {
        shows.show(request.showId());
        comments.insert(new ShowComment(null, request.showId(), request.comment().strip(), request.rating()));
        return Map.of("status", "created");
    }
}
