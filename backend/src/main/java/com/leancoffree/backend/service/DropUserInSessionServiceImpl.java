package com.leancoffree.backend.service;

import com.leancoffree.backend.controller.RefreshUsersInSessionException;
import com.leancoffree.backend.domain.entity.UsersEntity;
import com.leancoffree.backend.domain.entity.UsersEntity.UsersId;
import com.leancoffree.backend.domain.model.ListOfDisplayNamesBody;
import com.leancoffree.backend.domain.model.RefreshUsersRequest;
import com.leancoffree.backend.repository.UsersRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class DropUserInSessionServiceImpl implements DropUserInSessionService {

  private final UsersRepository usersRepository;

  public DropUserInSessionServiceImpl(final UsersRepository usersRepository) {
    this.usersRepository = usersRepository;
  }

  public ListOfDisplayNamesBody dropUserInSessionAndReturnAllUsers(
      final RefreshUsersRequest refreshUsersRequest) {

    final List<String> displayNames = new ArrayList<>();
    if (isRequestValid(refreshUsersRequest)) {
      final String displayName = refreshUsersRequest.getDisplayName();
      final String sessionId = refreshUsersRequest.getSessionId();

      usersRepository.deleteById(UsersId.builder()
          .displayName(displayName)
          .sessionId(sessionId)
          .build());

      final Optional<List<UsersEntity>> optionalUsersEntityList = usersRepository
          .findAllBySessionId(sessionId);

      if (optionalUsersEntityList.isPresent()) {
        for (final UsersEntity usersEntity : optionalUsersEntityList.get()) {
          displayNames.add(usersEntity.getDisplayName());
        }
      }
    }
    return ListOfDisplayNamesBody.builder().displayNames(displayNames)
        .build();
  }

  private boolean isRequestValid(final RefreshUsersRequest refreshUsersRequest) {
    return refreshUsersRequest != null
        && refreshUsersRequest.getDisplayName() != null
        && refreshUsersRequest.getSessionId() != null && !refreshUsersRequest
        .getDisplayName().isBlank() && !refreshUsersRequest.getSessionId().isBlank();
  }

}
