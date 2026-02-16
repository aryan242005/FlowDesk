package com.example.flow_desk_backend.security;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class LoginRateLimitingFilter extends OncePerRequestFilter {

    private final RateLimitService rateLimitService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        if ("/auth/login".equals(request.getRequestURI()) && "POST".equals(request.getMethod())) {
            // WRAP the request to allow multiple reads
            CachedBodyHttpServletRequest wrappedRequest = new CachedBodyHttpServletRequest(request);

            String ip = getClientIP(wrappedRequest);
            String email = extractEmail(wrappedRequest);

            if (email != null) {
                String key = "LOGIN_ATTEMPT:" + ip + ":" + email;
                if (!rateLimitService.isAllowed(key, 5, 60)) {
                    response.setStatus(429);
                    response.setContentType("application/json");
                    response.getWriter().write("{\"error\": \"Too many login login attempts.\"}");
                    return;
                }
            }
            // Pass the WRAPPED request so the Controller can read it too
            filterChain.doFilter(wrappedRequest, response);
            return;
        }

        filterChain.doFilter(request, response);
    }

    private String getClientIP(HttpServletRequest request) {
        String xff = request.getHeader("X-Forwarded-For");
        return (xff != null && !xff.isBlank()) ? xff.split(",")[0].trim() : request.getRemoteAddr();
    }

    private String extractEmail(HttpServletRequest request) throws IOException {
        JsonNode root = objectMapper.readTree(request.getInputStream());
        return (root != null && root.has("email")) ? root.get("email").asText() : null;
    }
}