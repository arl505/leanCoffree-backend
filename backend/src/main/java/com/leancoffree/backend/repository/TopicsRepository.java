package com.leancoffree.backend.repository;

import com.leancoffree.backend.domain.entity.TopicsEntity;
import com.leancoffree.backend.domain.entity.TopicsEntity.TopicsId;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface TopicsRepository extends CrudRepository<TopicsEntity, TopicsId> {

  Optional<List<TopicsEntity>> findAllBySessionIdOrderByCreatedTimestamp(
      final String sessionId);

  @Query(value = "SELECT topics.topic_text, votes.display_name"
      + " FROM topics"
      + " LEFT JOIN votes ON topics.session_id = votes.session_id AND topics.topic_text = votes.topic_text"
      + " WHERE  topics.session_id = :sessionId"
      + " ORDER BY topics.created_timestamp",
      nativeQuery = true)
  List<Object[]> findAllVotes(@Param("sessionId") final String sessionId);
}
