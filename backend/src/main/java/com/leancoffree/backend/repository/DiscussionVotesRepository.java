package com.leancoffree.backend.repository;

import com.leancoffree.backend.domain.entity.DiscussionVotesEntity;
import com.leancoffree.backend.domain.entity.DiscussionVotesEntity.DiscussionVotesId;
import com.leancoffree.backend.enums.DiscussionVoteType;
import java.util.List;
import org.springframework.data.repository.CrudRepository;

public interface DiscussionVotesRepository extends
    CrudRepository<DiscussionVotesEntity, DiscussionVotesId> {

  List<DiscussionVotesEntity> findAllBySessionIdAndVoteType(final String sessionId,
      final DiscussionVoteType voteType);
}
