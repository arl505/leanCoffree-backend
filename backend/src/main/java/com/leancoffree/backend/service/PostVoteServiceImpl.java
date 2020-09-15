package com.leancoffree.backend.service;

import static com.leancoffree.backend.enums.SuccessOrFailure.FAILURE;
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
  public SuccessOrFailureAndErrorBody postVote(final PostVoteRequest postVoteRequest) {

    final VotesEntity votesEntity = VotesEntity.builder()
        .voterDisplayName(postVoteRequest.getVoterDisplayName())
        .topicAuthorDisplayName(postVoteRequest.getAuthorDisplayName())
        .topicAuthorSessionId(postVoteRequest.getSessionId())
        .voterSessionId(postVoteRequest.getSessionId())
        .text(postVoteRequest.getText())
        .build();

    if (CAST.equals(postVoteRequest.getCommand())) {
      final Long votesCastPreviously = votesRepository
          .countByVoterDisplayNameAndVoterSessionId(postVoteRequest.getVoterDisplayName(),
              postVoteRequest.getSessionId());
      if (votesCastPreviously >= 3) {
        return new SuccessOrFailureAndErrorBody(FAILURE, "You can only vote thrice");
      }
      votesRepository.save(votesEntity);
    } else {
      votesRepository
          .deleteByVoterSessionIdAndTextAndVoterDisplayName(postVoteRequest.getSessionId(),
              postVoteRequest.getText(), postVoteRequest.getVoterDisplayName());
    }
    return broadcastTopicsService.broadcastTopics(postVoteRequest.getSessionId());
  }
}
