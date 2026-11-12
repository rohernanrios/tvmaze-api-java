package mx.tvmaze;

import jakarta.validation.constraints.NotBlank;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
class ShowController {
    private final TvMazeClient tvMaze;

    ShowController(TvMazeClient tvMaze) {
        this.tvMaze = tvMaze;
    }

    @GetMapping("/search")
    List<ShowSummary> search(@RequestParam("search_query") @NotBlank String query) {
        return tvMaze.search(query.strip()).stream().map(ShowSummary::from).toList();
    }
}
