package com.leancoffree.backend.config;

import static com.leancoffree.backend.enums.RefreshUsersCommand.DROP;

import com.leancoffree.backend.controller.RefreshUsersInSessionException;
import com.leancoffree.backend.domain.model.RefreshUsersRequest;
import com.leancoffree.backend.service.DropUserInSessionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

@Slf4j
@Component
public class WebSocketDisconnectListener {

  private final DropUserInSessionService dropUserInSessionService;

  public WebSocketDisconnectListener(final DropUserInSessionService dropUserInSessionService) {
    this.dropUserInSessionService = dropUserInSessionService;
  }

  @EventListener
  public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
    final StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
    final StompPrincipal userIdObject = (StompPrincipal) headerAccessor.getHeader("simpUser");
    try {
      dropUserInSessionService.dropUserInSessionAndReturnAllUsers(RefreshUsersRequest.builder()
          .command(DROP)
          .sessionId(headerAccessor.getSessionId())
          .websocketUserId(userIdObject.getName())
          .build());
    } catch (final RefreshUsersInSessionException e) {
      log.error("Caught RefreshUsersInSessionException in disconnect listener: ", e);
    }
  }
}
