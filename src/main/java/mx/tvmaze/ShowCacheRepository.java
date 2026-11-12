package mx.tvmaze;

import org.springframework.data.mongodb.repository.MongoRepository;

interface ShowCacheRepository extends MongoRepository<CachedShow, Long> {
}
