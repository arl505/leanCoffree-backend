package com.leancoffree.backend.repository;

import com.leancoffree.backend.domain.entity.UsersEntity;
import com.leancoffree.backend.domain.entity.UsersEntity.UsersId;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface UsersRepository extends CrudRepository<UsersEntity, UsersId> {

  Optional<List<UsersEntity>> findBySessionIdAndIsOnlineTrue(final String sessionId);
  long countBySessionIdAndIsOnlineTrueAndIsModeratorTrue(final String sessionId);
  Optional<List<UsersEntity>> findBySessionIdAndIsModeratorTrue(final String sessionId);
  Optional<UsersEntity> findByWebsocketUserId(final String websocketUserId);
  void deleteBySessionId(final String sessionId);

  @Query(value = "SELECT display_name, websocket_user_id " +
      "FROM users " +
      "WHERE session_id = :sessionId and is_online = true " +
      "ORDER BY display_name " +
      "LIMIT 1", nativeQuery = true)
  List<Object[]> getTopAlphabeticalOnlineUser(@Param("sessionId") final String sessionId);

}
