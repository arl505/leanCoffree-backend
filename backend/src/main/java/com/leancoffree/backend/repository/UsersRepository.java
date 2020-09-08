package com.leancoffree.backend.repository;

import com.leancoffree.backend.domain.entity.UsersEntity;
import com.leancoffree.backend.domain.entity.UsersEntity.UsersId;
import java.util.List;
import java.util.Optional;
import org.springframework.data.repository.CrudRepository;

public interface UsersRepository extends CrudRepository<UsersEntity, UsersId> {

  Optional<List<UsersEntity>> findAllBySessionId(final String sessionId);

  void deleteByWebsocketUserId(final String websocketUserId);
}
