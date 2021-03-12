package com.leancoffree.backend.service;

import com.leancoffree.backend.domain.model.PostDiscussionVoteRequest;
import com.leancoffree.backend.domain.model.SuccessOrFailureAndErrorBody;

public interface PostDiscussionVoteService {

  SuccessOrFailureAndErrorBody postVote(final PostDiscussionVoteRequest postDiscussionVoteRequest);
}
