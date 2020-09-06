package com.leancoffree.backend.repository;

import com.leancoffree.backend.domain.entity.SessionsEntity;
import org.springframework.data.repository.CrudRepository;

public interface SessionsRepository extends CrudRepository<SessionsEntity, String> {

}
