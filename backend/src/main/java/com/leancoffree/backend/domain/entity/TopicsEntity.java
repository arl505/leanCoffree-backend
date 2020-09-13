package com.leancoffree.backend.domain.entity;

import com.leancoffree.backend.domain.entity.TopicsEntity.TopicsId;
import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
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
@Table(name = "topics")
@IdClass(TopicsId.class)
@NoArgsConstructor
@AllArgsConstructor
public class TopicsEntity {

  @Id
  @Column(name = "session_id")
  private String sessionId;

  @Id
  @Column(name = "topic_text")
  private String text;

  @Data
  @Builder
  @AllArgsConstructor
  @NoArgsConstructor
  @EqualsAndHashCode
  public static class TopicsId implements Serializable {

    private String sessionId;
    private String text;
  }
}
