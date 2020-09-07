package com.leancoffree.backend.service;

import static com.leancoffree.backend.enums.SuccessOrFailure.FAILURE;
import static com.leancoffree.backend.enums.SuccessOrFailure.SUCCESS;

import com.leancoffree.backend.domain.entity.UsersEntity;
import com.leancoffree.backend.domain.entity.UsersEntity.UsersId;
import com.leancoffree.backend.domain.model.AddUserToSessionRequest;
import com.leancoffree.backend.domain.model.AddUserToSessionResponse;
import com.leancoffree.backend.repository.UsersRepository;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class AddUserToSessionServiceImpl implements AddUserToSessionService {

  private final UsersRepository usersRepository;

  public AddUserToSessionServiceImpl(final UsersRepository usersRepository) {
    this.usersRepository = usersRepository;
  }

  public AddUserToSessionResponse addUserToSession(
      final AddUserToSessionRequest addUserToSessionRequest) {

    if (isRequestValid(addUserToSessionRequest)) {
      final String displayName = addUserToSessionRequest.getDisplayName();
      final String sessionId = addUserToSessionRequest.getSessionId();

      final UsersId usersId = new UsersId(displayName, sessionId);
      final Optional<UsersEntity> usersEntityOptional = usersRepository.findById(usersId);

      if (usersEntityOptional.isEmpty()) {
        usersRepository.save(UsersEntity.builder()
            .displayName(displayName)
            .sessionId(sessionId)
            .build());
        return new AddUserToSessionResponse(SUCCESS, null);
      } else {
        return new AddUserToSessionResponse(FAILURE,
            "Display name already in use for this session");
      }

    } else {
      return new AddUserToSessionResponse(FAILURE, "Invalid request");
    }
  }

  private boolean isRequestValid(final AddUserToSessionRequest addUserToSessionRequest) {
    return addUserToSessionRequest != null && addUserToSessionRequest.getDisplayName() != null
        && addUserToSessionRequest.getSessionId() != null && !addUserToSessionRequest
        .getDisplayName().isBlank() && !addUserToSessionRequest.getSessionId().isBlank();
  }
}
