package mx.tvmaze;

import org.springframework.data.mongodb.repository.MongoRepository;

interface ShowCommentRepository extends MongoRepository<ShowComment, String> {
}
