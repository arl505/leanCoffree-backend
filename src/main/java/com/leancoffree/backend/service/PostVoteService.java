package com.leancoffree.backend.service;

import com.leancoffree.backend.domain.model.PostVoteRequest;
import com.leancoffree.backend.domain.model.SuccessOrFailureAndErrorBody;

public interface PostVoteService {

  SuccessOrFailureAndErrorBody postVote(final PostVoteRequest postVoteRequest);
}
