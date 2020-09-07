package com.leancoffree.backend.service;

import com.leancoffree.backend.domain.entity.UsersEntity;
import com.leancoffree.backend.domain.entity.UsersEntity.UsersId;
import com.leancoffree.backend.domain.model.NewUserRequestNotification;
import com.leancoffree.backend.domain.model.NewUserResponseNotification;
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

  public NewUserResponseNotification addUserToSessionAndReturnAllUsers(
      final NewUserRequestNotification newUserRequestNotification) {

    if (isRequestValid(newUserRequestNotification)) {
      final String displayName = newUserRequestNotification.getDisplayName();
      final String sessionId = newUserRequestNotification.getSessionId();

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
          return NewUserResponseNotification.builder().displayNames(displayNames)
              .build();
        }
      }
    }
    return null;
  }

  private boolean isRequestValid(final NewUserRequestNotification newUserRequestNotification) {
    return newUserRequestNotification != null && newUserRequestNotification.getDisplayName() != null
        && newUserRequestNotification.getSessionId() != null && !newUserRequestNotification
        .getDisplayName().isBlank() && !newUserRequestNotification.getSessionId().isBlank();
  }
}
