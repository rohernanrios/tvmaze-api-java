package mx.tvmaze;

import com.fasterxml.jackson.databind.JsonNode;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
class ShowController {
    private final TvMazeClient tvMaze;
    private final ShowService shows;

    ShowController(TvMazeClient tvMaze, ShowService shows) {
        this.tvMaze = tvMaze;
        this.shows = shows;
    }

    @GetMapping("/show/{show_id}")
    JsonNode show(@PathVariable("show_id") @Positive long id) {
        return shows.show(id);
    }

    @GetMapping("/search")
    List<ShowSummary> search(@RequestParam("search_query") @NotBlank String query) {
        return tvMaze.search(query.strip()).stream().map(ShowSummary::from).toList();
    }
}
