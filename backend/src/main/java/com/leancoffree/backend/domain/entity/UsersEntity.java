package com.leancoffree.backend.domain.entity;

import com.leancoffree.backend.domain.entity.UsersEntity.UsersId;
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
import org.hibernate.annotations.Type;

@Data
@Builder
@Entity
@Table(name = "users")
@NoArgsConstructor
@AllArgsConstructor
@IdClass(UsersId.class)
public class UsersEntity {

  @Id
  @Column(name = "display_name")
  private String displayName;

  @Id
  @Column(name = "session_id")
  private String sessionId;

  @Column(name = "votes_used")
  private Integer votesUsed;

  @Column(name = "websocket_user_id")
  private String websocketUserId;

  @Column(name = "is_online")
  @Type(type = "org.hibernate.type.NumericBooleanType")
  private Boolean isOnline;

  @Data
  @Builder
  @AllArgsConstructor
  @NoArgsConstructor
  @EqualsAndHashCode
  public static class UsersId implements Serializable {

    private String displayName;
    private String sessionId;
  }
}
