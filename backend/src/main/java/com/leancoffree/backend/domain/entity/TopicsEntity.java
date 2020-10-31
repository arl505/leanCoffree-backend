package com.leancoffree.backend.domain.entity;

import static javax.persistence.EnumType.STRING;

import com.leancoffree.backend.domain.entity.TopicsEntity.TopicsId;
import com.leancoffree.backend.enums.TopicStatus;
import java.io.Serializable;
import java.time.Instant;
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
import org.hibernate.annotations.CreationTimestamp;

@Data
@Builder
@Entity
@Table(name = "topics")
@IdClass(TopicsId.class)
@NoArgsConstructor
@AllArgsConstructor
public class TopicsEntity {

  @Id
  @Column(name = "session_id")
  private String sessionId;

  @Id
  @Column(name = "text")
  private String text;

  @Id
  @Column(name = "display_name")
  private String displayName;

  @CreationTimestamp
  @Column(name = "created_timestamp")
  private Instant createdTimestamp;

  @Enumerated(STRING)
  @Column(name = "status")
  private TopicStatus topicStatus;

  @Column(name = "y_index")
  private Integer verticalIndex;

  @Column(name = "finished_at")
  private Instant finishedAt;

  @Data
  @Builder
  @AllArgsConstructor
  @NoArgsConstructor
  @EqualsAndHashCode
  public static class TopicsId implements Serializable {

    private String sessionId;
    private String text;
    private String displayName;
  }
}
