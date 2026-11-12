package mx.tvmaze;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.mapping;
import static java.util.stream.Collectors.toList;

@RestController
class ShowController {
    private final TvMazeClient tvMaze;
    private final ShowService shows;
    private final ShowCommentRepository comments;
    private final ObjectMapper mapper;

    ShowController(TvMazeClient tvMaze, ShowService shows, ShowCommentRepository comments, ObjectMapper mapper) {
        this.tvMaze = tvMaze;
        this.shows = shows;
        this.comments = comments;
        this.mapper = mapper;
    }

    @GetMapping("/show/{show_id}")
    JsonNode show(@PathVariable("show_id") @Positive long id) {
        ObjectNode show = (ObjectNode) shows.show(id).deepCopy();
        List<CommentView> savedComments = comments.findByShowIdOrderByIdAsc(id).stream()
                .map(CommentView::from).toList();
        show.set("comments", mapper.valueToTree(savedComments));
        return show;
    }

    @GetMapping("/search")
    List<ShowSummary> search(@RequestParam("search_query") @NotBlank String query) {
        List<JsonNode> results = tvMaze.search(query.strip());
        if (results.isEmpty()) {
            return List.of();
        }
        List<Long> ids = results.stream().map(show -> show.path("id").asLong()).distinct().toList();
        Map<Long, List<CommentView>> byShow = comments.findByShowIdInOrderByIdAsc(ids).stream()
                .collect(groupingBy(ShowComment::showId, mapping(CommentView::from, toList())));
        return results.stream()
                .map(show -> ShowSummary.from(show, byShow.getOrDefault(show.path("id").asLong(), List.of())))
                .toList();
    }
}
