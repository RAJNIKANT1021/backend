package com.example.demo.controller;

import com.example.demo.dto.LoginRequest;
import com.example.demo.dto.SignupRequest;
import com.example.demo.service.AuthService;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @GetMapping("/test")
    public ResponseEntity<String> test() {
        return ResponseEntity.ok("Auth controller is working!");
    }
    @PostMapping("/signup")
    public ResponseEntity<String> signup(
            @Valid @RequestBody SignupRequest request,
            HttpServletResponse response
    ) {
        authService.signup(request);

        // auto-login
        LoginRequest logindata = new LoginRequest();
        logindata.setEmail(request.getEmail());
        logindata.setPassword(request.getPassword());
        String token = authService.login(
                logindata
        );

        Cookie cookie = new Cookie("JWT_TOKEN", token);
        cookie.setHttpOnly(true);
        cookie.setSecure(false); // true in prod
        cookie.setPath("/");
        cookie.setMaxAge(60 * 60);

        response.addCookie(cookie);

        return ResponseEntity.ok("Signup & Login successful");
    }


    @PostMapping("/login")
    public ResponseEntity<String> login(
            @Valid @RequestBody LoginRequest request,
            HttpServletResponse response
    ) {
        String token = authService.login(request);

        Cookie cookie = new Cookie("JWT_TOKEN", token);
        cookie.setHttpOnly(true);   // JS cannot access
        cookie.setSecure(false);    // true in prod (HTTPS)
        cookie.setPath("/");
        cookie.setMaxAge(60 * 60);  // 1 hour

        response.addCookie(cookie);

        return ResponseEntity.ok("Login successful");
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpServletResponse response) {

        // Create cookie with same name
        Cookie cookie = new Cookie("JWT_TOKEN", null);

        // IMPORTANT: must match login cookie
        cookie.setHttpOnly(true);
        cookie.setSecure(false); // true in prod
        cookie.setPath("/");

        // MaxAge = 0 → delete cookie
        cookie.setMaxAge(0);

        response.addCookie(cookie);

        return ResponseEntity.ok("Logged out successfully");
    }


}
