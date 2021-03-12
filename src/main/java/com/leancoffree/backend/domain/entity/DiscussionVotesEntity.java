package com.leancoffree.backend.domain.entity;

import static javax.persistence.EnumType.STRING;

import com.leancoffree.backend.domain.entity.DiscussionVotesEntity.DiscussionVotesId;
import com.leancoffree.backend.enums.DiscussionVoteType;
import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@Builder
@Entity
@Table(name = "discussion_votes")
@IdClass(DiscussionVotesId.class)
@NoArgsConstructor
@AllArgsConstructor
public class DiscussionVotesEntity {

  @Id
  @Column(name = "session_id")
  private String sessionId;

  @Id
  @Column(name = "user_display_name")
  private String userDisplayName;

  @Enumerated(STRING)
  @Column(name = "vote_type")
  private DiscussionVoteType voteType;

  @Data
  @Builder
  @AllArgsConstructor
  @NoArgsConstructor
  @EqualsAndHashCode
  public static class DiscussionVotesId implements Serializable {

    private String sessionId;
    private String userDisplayName;
  }

}
