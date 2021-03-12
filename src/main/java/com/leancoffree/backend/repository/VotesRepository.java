package com.leancoffree.backend.repository;

import com.leancoffree.backend.domain.entity.VotesEntity;
import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface VotesRepository extends CrudRepository<VotesEntity, Long> {

  Long countByVoterDisplayNameAndVoterSessionId(final String displayName,
      final String voterSessionId);

  void deleteByVoterSessionIdAndTextAndVoterDisplayName(final String voterSessionId,
      final String text, final String voterDisplayName);

  void deleteByVoterSessionIdAndText(final String sessionId, final String text);

  void deleteByVoterSessionId(final String sessionId);

  @Query(value = "SELECT topic_text, count(*) " +
      "FROM votes " +
      "WHERE voter_session_id = :sessionId " +
      "GROUP BY topic_text " +
      "ORDER BY count(*) desc, topic_text",
      nativeQuery = true)
  List<Object[]> findVotesInOrder(@Param("sessionId") final String sessionId);
}
