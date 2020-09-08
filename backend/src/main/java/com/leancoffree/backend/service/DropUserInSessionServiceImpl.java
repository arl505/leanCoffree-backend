package com.leancoffree.backend.service;

import com.leancoffree.backend.domain.entity.UsersEntity;
import com.leancoffree.backend.domain.model.RefreshUsersRequest;
import com.leancoffree.backend.repository.UsersRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.transaction.Transactional;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

@Service
public class DropUserInSessionServiceImpl implements DropUserInSessionService {

  private final UsersRepository usersRepository;

  public DropUserInSessionServiceImpl(final UsersRepository usersRepository) {
    this.usersRepository = usersRepository;

  }

  @Transactional
  public String dropUserInSessionAndReturnAllUsers(final RefreshUsersRequest refreshUsersRequest) {
    final List<String> displayNames = new ArrayList<>();
    if (isRequestValid(refreshUsersRequest)) {
      usersRepository.deleteByWebsocketUserId(refreshUsersRequest.getWebsocketUserId());

      final Optional<List<UsersEntity>> optionalUsersEntityList = usersRepository
          .findAllBySessionId(refreshUsersRequest.getSessionId());

      if (optionalUsersEntityList.isPresent()) {
        for (final UsersEntity usersEntity : optionalUsersEntityList.get()) {
          displayNames.add(usersEntity.getDisplayName());
        }
      }
    }
    return new JSONObject().put("displayNames", new JSONArray(displayNames)).toString();
  }

  private boolean isRequestValid(final RefreshUsersRequest refreshUsersRequest) {
    return refreshUsersRequest != null && refreshUsersRequest.getDisplayName() != null
        && refreshUsersRequest.getSessionId() != null && !refreshUsersRequest.getDisplayName()
        .isBlank() && !refreshUsersRequest.getSessionId().isBlank();
  }

}
