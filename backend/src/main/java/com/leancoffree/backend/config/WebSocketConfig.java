package com.leancoffree.backend.config;

import static com.leancoffree.backend.enums.RefreshUsersCommand.DROP;

import com.leancoffree.backend.controller.RefreshUsersInSessionException;
import com.leancoffree.backend.domain.model.RefreshUsersRequest;
import com.leancoffree.backend.service.RefreshUsersInSessionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

@Slf4j
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

  private final RefreshUsersInSessionService refreshUsersInSessionService;

  public WebSocketConfig(final RefreshUsersInSessionService refreshUsersInSessionService) {
    this.refreshUsersInSessionService = refreshUsersInSessionService;
  }

  @Override
  public void configureMessageBroker(final MessageBrokerRegistry config) {
    config.enableSimpleBroker("/topic/session");
    config.setApplicationDestinationPrefixes("/ws");
  }

  @Override
  public void registerStompEndpoints(final StompEndpointRegistry registry) {
    registry.addEndpoint("/lean-coffree").setHandshakeHandler(new CustomHandshakeHandler())
        .setAllowedOrigins("*").withSockJS();
  }

  @EventListener
  public void handleWebSocketDisconnectListener(final SessionDisconnectEvent event) {
    StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
    try {
      refreshUsersInSessionService.refreshUsersInSession(RefreshUsersRequest.builder()
          .command(DROP)
          .sessionId(headerAccessor.getSessionId())
          .websocketUserId(headerAccessor.getHeader("simpUser").toString())
          .build());
    } catch (final RefreshUsersInSessionException e) {
      log.error("Caught RefreshUsersInSessionException: ", e);
    }
  }

}