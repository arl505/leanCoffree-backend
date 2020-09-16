package com.leancoffree.backend.repository;

import com.leancoffree.backend.domain.entity.TopicsEntity;
import com.leancoffree.backend.domain.entity.TopicsEntity.TopicsId;
import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface TopicsRepository extends CrudRepository<TopicsEntity, TopicsId> {

  @Query(value = "SELECT topics.text, votes.voter_display_name, topics.status, topics.display_name, topics.created_timestamp"
      + " FROM topics"
      + " LEFT JOIN votes ON topics.session_id = votes.topic_author_session_id AND topics.text = votes.topic_text"
      + " WHERE  topics.session_id = :sessionId"
      + " ORDER BY topics.created_timestamp",
      nativeQuery = true)
  List<Object[]> findAllVotes(@Param("sessionId") final String sessionId);
}
