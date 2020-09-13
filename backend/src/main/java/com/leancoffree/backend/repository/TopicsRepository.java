package com.leancoffree.backend.repository;

import com.leancoffree.backend.domain.entity.TopicsEntity;
import com.leancoffree.backend.domain.entity.TopicsEntity.TopicsId;
import java.util.List;
import java.util.Optional;
import org.springframework.data.repository.CrudRepository;

public interface TopicsRepository extends CrudRepository<TopicsEntity, TopicsId> {

  Optional<List<TopicsEntity>> findAllBySessionIdOrderByCreatedTimestamp(
      final String sessionId);
}
