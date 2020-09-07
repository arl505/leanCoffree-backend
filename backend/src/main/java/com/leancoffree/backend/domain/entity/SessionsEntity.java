package com.leancoffree.backend.domain.entity;

import static javax.persistence.EnumType.STRING;

import com.leancoffree.backend.enums.SessionStatus;
import java.time.Instant;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Data
@Builder
@Entity
@Table(name = "sessions")
@NoArgsConstructor
@AllArgsConstructor
public class SessionsEntity {

  @Id
  @Column(name = "id")
  private String id;

  @Enumerated(STRING)
  @Column(name = "status")
  private SessionStatus sessionStatus;

  @CreationTimestamp
  @Column(name = "created_timestamp")
  private Instant createdTimestamp;

  @UpdateTimestamp
  @Column(name = "updated_timestamp")
  private Instant updatedTimestamp;
}
