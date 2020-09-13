package com.leancoffree.backend.repository;

import com.leancoffree.backend.domain.entity.VotesEntity;
import com.leancoffree.backend.domain.entity.VotesEntity.VotesId;
import org.springframework.data.repository.CrudRepository;

public interface VotesRepository extends CrudRepository<VotesEntity, VotesId> {

  void deleteByDisplayName(final String displayName);
}
