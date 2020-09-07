package com.leancoffree.backend.service;

import com.leancoffree.backend.domain.entity.UsersEntity;
import com.leancoffree.backend.domain.entity.UsersEntity.UsersId;
import com.leancoffree.backend.domain.model.DisplayNameAndSessionIdBody;
import com.leancoffree.backend.domain.model.ListOfDisplayNamesBody;
import com.leancoffree.backend.repository.UsersRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class AddUserToSessionServiceImpl implements AddUserToSessionService {

  private final UsersRepository usersRepository;

  public AddUserToSessionServiceImpl(final UsersRepository usersRepository) {
    this.usersRepository = usersRepository;
  }

  public ListOfDisplayNamesBody addUserToSessionAndReturnAllUsers(
      final DisplayNameAndSessionIdBody displayNameAndSessionIdBody) throws Exception {

    if (isRequestValid(displayNameAndSessionIdBody)) {
      final String displayName = displayNameAndSessionIdBody.getDisplayName();
      final String sessionId = displayNameAndSessionIdBody.getSessionId();

      final UsersId usersId = new UsersId(displayName, sessionId);
      final Optional<UsersEntity> usersEntityOptional = usersRepository.findById(usersId);

      if (usersEntityOptional.isEmpty()) {
        usersRepository.save(UsersEntity.builder()
            .displayName(displayName)
            .sessionId(sessionId)
            .votesUsed(0)
            .build());

        final Optional<List<UsersEntity>> optionalUsersEntityList = usersRepository
            .findAllBySessionId(sessionId);

        if (optionalUsersEntityList.isPresent()) {
          final List<String> displayNames = new ArrayList<>();
          for(final UsersEntity usersEntity : optionalUsersEntityList.get()) {
            displayNames.add(usersEntity.getDisplayName());
          }
          return ListOfDisplayNamesBody.builder().displayNames(displayNames)
              .build();
        } else {
          throw new Exception("How'd that happen? Please try again");
        }

      } else {
        throw new Exception("Display name already in use for session");
      }
    }
    throw new Exception("Invalid request");
  }

  private boolean isRequestValid(final DisplayNameAndSessionIdBody displayNameAndSessionIdBody) {
    return displayNameAndSessionIdBody != null && displayNameAndSessionIdBody.getDisplayName() != null
        && displayNameAndSessionIdBody.getSessionId() != null && !displayNameAndSessionIdBody
        .getDisplayName().isBlank() && !displayNameAndSessionIdBody.getSessionId().isBlank();
  }
}
