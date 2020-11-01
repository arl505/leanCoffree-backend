package com.leancoffree.backend.service;

import static com.leancoffree.backend.enums.DiscussionVoteType.MORE_TIME;
import static com.leancoffree.backend.enums.DiscussionVoteType.NEXT_TOPIC;
import static com.leancoffree.backend.enums.SuccessOrFailure.SUCCESS;

import com.leancoffree.backend.domain.entity.DiscussionVotesEntity;
import com.leancoffree.backend.domain.model.PostDiscussionVoteRequest;
import com.leancoffree.backend.domain.model.SuccessOrFailureAndErrorBody;
import com.leancoffree.backend.repository.DiscussionVotesRepository;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class PostDiscussionVoteServiceImpl implements PostDiscussionVoteService {

  private static final String websocketDestination = "/topic/discussion-votes/session/";

  private final DiscussionVotesRepository discussionVotesRepository;
  private final SimpMessagingTemplate webSocketMessagingTemplate;

  public PostDiscussionVoteServiceImpl(final DiscussionVotesRepository discussionVotesRepository,
      final SimpMessagingTemplate webSocketMessagingTemplate) {
    this.discussionVotesRepository = discussionVotesRepository;
    this.webSocketMessagingTemplate = webSocketMessagingTemplate;
  }

  public SuccessOrFailureAndErrorBody postVote(
      final PostDiscussionVoteRequest postDiscussionVoteRequest) {

    final String sessionId = postDiscussionVoteRequest.getSessionId();

    final DiscussionVotesEntity discussionVotesEntity = DiscussionVotesEntity.builder()
        .sessionId(sessionId)
        .userDisplayName(postDiscussionVoteRequest.getUserDisplayName())
        .voteType(postDiscussionVoteRequest.getVoteType())
        .build();

    discussionVotesRepository.save(discussionVotesEntity);


    final List<DiscussionVotesEntity> nextTopicVotes = discussionVotesRepository
        .findAllBySessionIdAndVoteType(sessionId, NEXT_TOPIC);
    final List<DiscussionVotesEntity> moreTimeVotes = discussionVotesRepository
        .findAllBySessionIdAndVoteType(sessionId, MORE_TIME);

    final JSONObject messageJson = new JSONObject()
        .put("moreTimeVotesCount", moreTimeVotes.size())
        .put("nextTopicVotesCount", nextTopicVotes.size());

    webSocketMessagingTemplate
        .convertAndSend(websocketDestination + sessionId, messageJson.toString());

    return new SuccessOrFailureAndErrorBody(SUCCESS, null);
  }
}
