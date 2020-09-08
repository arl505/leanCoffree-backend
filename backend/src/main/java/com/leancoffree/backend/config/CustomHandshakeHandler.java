package com.leancoffree.backend.config;

import java.security.Principal;
import java.util.Map;
import java.util.UUID;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;

class CustomHandshakeHandler extends DefaultHandshakeHandler {

  @Override
  protected Principal determineUser(final ServerHttpRequest request,
      final WebSocketHandler wsHandler, final Map<String, Object> attributes) {
    return new StompPrincipal(UUID.randomUUID().toString());
  }
}