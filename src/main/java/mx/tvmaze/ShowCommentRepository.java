package mx.tvmaze;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Collection;
import java.util.List;

interface ShowCommentRepository extends MongoRepository<ShowComment, String> {
    List<ShowComment> findByShowIdOrderByIdAsc(long showId);
    List<ShowComment> findByShowIdInOrderByIdAsc(Collection<Long> showIds);
}
