package com.leancoffree.backend.repository;

import com.leancoffree.backend.domain.entity.VotesEntity;
import org.springframework.data.repository.CrudRepository;

public interface VotesRepository extends CrudRepository<VotesEntity, Long> {

  Long countByVoterDisplayNameAndVoterSessionId(final String displayName,
      final String voterSessionId);

  void deleteByVoterSessionIdAndTextAndVoterDisplayName(final String voterSessionId,
      final String text, final String voterDisplayName);
}
