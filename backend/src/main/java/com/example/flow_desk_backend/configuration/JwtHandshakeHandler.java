package com.example.flow_desk_backend.configuration;

import java.security.Principal;
import java.util.Arrays;
import java.util.Map;

import com.example.flow_desk_backend.util.JwtUtil;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class JwtHandshakeHandler extends DefaultHandshakeHandler {

    private final JwtUtil jwtUtil;

    @Override
    protected Principal determineUser(ServerHttpRequest request, WebSocketHandler wsHandler, Map<String, Object> attributes) {
        String query = request.getURI().getQuery();
        if (query == null) return null;

        String token = Arrays.stream(query.split("&"))
                .filter(param -> param.startsWith("token="))
                .findFirst()
                .map(param -> param.substring(6))
                .orElse(null);

        if (token != null && jwtUtil.validateToken(token)) {
            return () -> jwtUtil.extractUsername(token);
        }
        return null;
    }
}
