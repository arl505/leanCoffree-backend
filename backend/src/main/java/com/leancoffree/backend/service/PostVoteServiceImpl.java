package com.leancoffree.backend.service;

import static com.leancoffree.backend.enums.VoteType.CAST;

import com.leancoffree.backend.domain.entity.VotesEntity;
import com.leancoffree.backend.domain.model.PostVoteRequest;
import com.leancoffree.backend.domain.model.SuccessOrFailureAndErrorBody;
import com.leancoffree.backend.repository.VotesRepository;
import javax.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
public class PostVoteServiceImpl implements PostVoteService {

  private final VotesRepository votesRepository;
  private final BroadcastTopicsService broadcastTopicsService;

  public PostVoteServiceImpl(final VotesRepository votesRepository,
      final BroadcastTopicsService broadcastTopicsService) {
    this.votesRepository = votesRepository;
    this.broadcastTopicsService = broadcastTopicsService;
  }

  @Transactional
  public SuccessOrFailureAndErrorBody postVote(PostVoteRequest postVoteRequest) {
    final VotesEntity votesEntity = VotesEntity.builder()
        .displayName(postVoteRequest.getVoterDisplayName())
        .sessionId(postVoteRequest.getSessionId())
        .text(postVoteRequest.getText())
        .build();

    if (CAST.equals(postVoteRequest.getCommand())) {
      votesRepository.save(votesEntity);
    } else {
      votesRepository.delete(votesEntity);
    }
    return broadcastTopicsService.broadcastTopics(postVoteRequest.getSessionId());
  }
}
