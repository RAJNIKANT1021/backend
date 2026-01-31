package com.example.demo.security;

import com.example.demo.repository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
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

        // 🔥🔥🔥 VERY IMPORTANT: allow auth endpoints
        String path = request.getServletPath();
        if (path.startsWith("/auth/")) {
            filterChain.doFilter(request, response);
            return;
        }

        // 🔐 Extract JWT from cookie
        String token = null;
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if ("JWT_TOKEN".equals(cookie.getName())) {
                    token = cookie.getValue();
                    break;
                }
            }
        }

        // ❌ No token → block
        if (token == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        try {
            String email = jwtUtil.extractEmail(token);

            if (email != null &&
                    SecurityContextHolder.getContext().getAuthentication() == null) {

                String finalToken = token;
                userRepository.findByEmail(email).ifPresent(user -> {

                    if (jwtUtil.validateToken(finalToken, user.getEmail())) {

                        UsernamePasswordAuthenticationToken authentication =
                                new UsernamePasswordAuthenticationToken(
                                        user.getEmail(),
                                        null,
                                        List.of(() -> "ROLE_USER")
                                );

                        SecurityContextHolder.getContext()
                                .setAuthentication(authentication);
                    }
                });
            }

        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        filterChain.doFilter(request, response);
    }
}
