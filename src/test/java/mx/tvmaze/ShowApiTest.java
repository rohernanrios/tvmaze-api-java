package mx.tvmaze;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ShowApiTest {
    private final ObjectMapper mapper = new ObjectMapper();
    private final TvMazeClient tvMaze = mock(TvMazeClient.class);
    private final ShowCacheRepository cache = mock(ShowCacheRepository.class);
    private final ShowCommentRepository comments = mock(ShowCommentRepository.class);
    private MockMvc mvc;

    @BeforeEach
    void setUp() {
        var shows = new ShowService(tvMaze, cache, mapper);
        mvc = MockMvcBuilders.standaloneSetup(
                new ShowController(tvMaze, shows, comments, mapper),
                new CommentController(comments, shows)
        ).build();
    }

    @Test
    void searchAddsCommentsByShowAndUsesWebChannelWhenNetworkIsMissing() throws Exception {
        var first = mapper.readTree("""
                {"id":1,"name":"First","network":null,"webChannel":{"name":"Web"},"genres":["Drama"]}
                """);
        var second = mapper.readTree("""
                {"id":2,"name":"Second","network":{"name":"TV"},"genres":[]}
                """);
        when(tvMaze.search("test")).thenReturn(List.of(first, second));
        when(comments.findByShowIdInOrderByIdAsc(List.of(1L, 2L)))
                .thenReturn(List.of(new ShowComment("a", 2, "Excelente", 5)));

        mvc.perform(get("/search").param("search_query", "test"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].channel").value("Web"))
                .andExpect(jsonPath("$[0].comments").isEmpty())
                .andExpect(jsonPath("$[1].channel").value("TV"))
                .andExpect(jsonPath("$[1].comments[0].rating").value(5));
    }

    @Test
    void showStoresUpstreamJsonWithoutLocalComments() throws Exception {
        var show = mapper.readTree("""
                {"id":1,"name":"First","externals":{"imdb":"tt123"}}
                """);
        when(tvMaze.show(1)).thenReturn(show);
        when(comments.findByShowIdOrderByIdAsc(1))
                .thenReturn(List.of(new ShowComment("a", 1, "Buena", 4)));

        mvc.perform(get("/show/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.externals.imdb").value("tt123"))
                .andExpect(jsonPath("$.comments[0].comment").value("Buena"));

        verify(cache).save(new CachedShow(1L, show.toString()));
    }

    @Test
    void cachedShowUsesCurrentCommentsWithoutCallingTvMaze() throws Exception {
        when(cache.findById(1L)).thenReturn(Optional.of(new CachedShow(1L, "{\"id\":1}")));
        when(comments.findByShowIdOrderByIdAsc(1))
                .thenReturn(List.of(new ShowComment("a", 1, "Nuevo", 0)));

        mvc.perform(get("/show/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.comments[0].rating").value(0));

        verifyNoInteractions(tvMaze);
    }

    @Test
    void invalidRatingDoesNotWriteToMongo() throws Exception {
        mvc.perform(post("/comments").contentType("application/json")
                        .content("{\"show_id\":1,\"comment\":\"Buena\",\"rating\":6}"))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(cache, comments, tvMaze);
    }

    @Test
    void validCommentReturnsCreatedAndPersistsWithShowId() throws Exception {
        when(cache.findById(1L)).thenReturn(Optional.of(new CachedShow(1L, "{\"id\":1}")));

        mvc.perform(post("/comments").contentType("application/json")
                        .content("{\"show_id\":1,\"comment\":\"Buena\",\"rating\":5}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("created"));

        verify(comments).insert(new ShowComment(null, 1, "Buena", 5));
    }
}
