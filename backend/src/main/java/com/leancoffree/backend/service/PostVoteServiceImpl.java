package com.leancoffree.backend.service;

import com.leancoffree.backend.domain.entity.VotesEntity;
import com.leancoffree.backend.domain.model.PostVoteRequest;
import com.leancoffree.backend.domain.model.SuccessOrFailureAndErrorBody;
import com.leancoffree.backend.repository.VotesRepository;
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

  public SuccessOrFailureAndErrorBody postVote(PostVoteRequest postVoteRequest) {
    votesRepository.save(VotesEntity.builder()
        .displayName(postVoteRequest.getVoterDisplayName())
        .sessionId(postVoteRequest.getSessionId())
        .text(postVoteRequest.getText())
        .build());
    return broadcastTopicsService.broadcastTopics(postVoteRequest.getSessionId());
  }
}
