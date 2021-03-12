package com.leancoffree.backend.domain.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@Entity
@Table(name = "votes")
@NoArgsConstructor
@AllArgsConstructor
public class VotesEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private Long id;

  @Column(name = "topic_text")
  private String text;

  @Column(name = "voter_session_id")
  private String voterSessionId;

  @Column(name = "voter_display_name")
  private String voterDisplayName;

  @Column(name = "topic_author_session_id")
  private String topicAuthorSessionId;

  @Column(name = "topic_author_display_name")
  private String topicAuthorDisplayName;
}
