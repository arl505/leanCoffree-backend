package com.leancoffree.backend.repository;

import com.leancoffree.backend.domain.entity.TopicsEntity;
import com.leancoffree.backend.domain.entity.TopicsEntity.TopicsId;
import java.sql.Timestamp;
import java.util.List;
import javax.transaction.Transactional;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface TopicsRepository extends CrudRepository<TopicsEntity, TopicsId> {

  @Query(value =
      "SELECT topics.text, votes.voter_display_name, topics.status, topics.display_name, topics.created_timestamp, topics.y_index, topics.finished_at"
          + " FROM topics"
          + " LEFT JOIN votes ON topics.session_id = votes.topic_author_session_id AND topics.text = votes.topic_text"
          + " WHERE  topics.session_id = :sessionId"
          + " ORDER BY topics.created_timestamp",
      nativeQuery = true)
  List<Object[]> findAllVotes(@Param("sessionId") final String sessionId);

  @Modifying
  @Query(value = "UPDATE topics " +
      "SET status = :command, finished_at = :finishedAt " +
      "WHERE text = :text and session_id = :sessionId and display_name = :displayName",
      nativeQuery = true)
  void updateStatusByTextAndSessionIdAndDisplayName(@Param("command") final String command,
      @Param("text") final String text, @Param("sessionId") final String sessionId,
      @Param("displayName") final String displayName,
      @Param("finishedAt") final Timestamp finishedAt);

  @Transactional
  @Modifying
  @Query(value = "UPDATE topics " +
      "SET y_index = :yIndex " +
      "WHERE text = :text and session_id = :sessionId",
      nativeQuery = true)
  void updateYIndexByTextAndSessionId(@Param("yIndex") final long yIndex,
      @Param("text") final String text, @Param("sessionId") final String sessionId);

  List<TopicsEntity> findAllBySessionIdOrderByText(final String sessionId);

  List<TopicsEntity> findAllBySessionIdOrderByVerticalIndex(final String sessionId);

  void deleteBySessionId(final String sessionId);
}
