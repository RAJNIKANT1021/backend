package com.example.demo.security;

import com.example.demo.repository.UserRepository;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import lombok.RequiredArgsConstructor;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        // ✅ 1. Allow public auth endpoints
        if (request.getServletPath().startsWith("/auth/")) {
            filterChain.doFilter(request, response);
            return;
        }

        // ✅ 2. Read Authorization header
        String authHeader = request.getHeader("Authorization");

        // ❌ 3. Block request if token missing or invalid format
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        try {
            // ✅ 4. Extract token
            String token = authHeader.substring(7);

            // ✅ 5. Extract email from JWT
            String email = jwtUtil.extractEmail(token);

            // ✅ 6. Authenticate only once per request
            if (email != null &&
                    SecurityContextHolder.getContext().getAuthentication() == null) {

                userRepository.findByEmail(email).ifPresent(user -> {

                    // ✅ 7. Validate token
                    if (jwtUtil.validateToken(token, user.getEmail())) {

                        // ✅ 8. Create Authentication object
                        UsernamePasswordAuthenticationToken authentication =
                                new UsernamePasswordAuthenticationToken(
                                        user.getEmail(),
                                        null,
                                        List.of(() -> "ROLE_USER")
                                );

                        // ✅ 9. Set authentication in context
                        SecurityContextHolder.getContext()
                                .setAuthentication(authentication);
                    }
                });
            }

        } catch (Exception ex) {
            // ❌ Invalid / expired / tampered token
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        // ✅ 10. Continue filter chain
        filterChain.doFilter(request, response);
    }
}
