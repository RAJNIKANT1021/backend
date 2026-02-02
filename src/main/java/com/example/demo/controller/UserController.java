package com.example.demo.controller;

import com.example.demo.dto.ApiResponse;
import com.example.demo.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
@CrossOrigin
@RestController
@RequestMapping("/api")
public class UserController {

    private final UserService userService;
    public UserController(UserService userService) {
        this.userService = userService;
    }

    // 🔐 Protected API
    @GetMapping("/profile")
    public ResponseEntity<ApiResponse<Object>> getProfile(
            Authentication authentication
    ) {
        // authentication.getName() = email (from JwtFilter)
        String email = authentication.getName();

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Profile fetched successfully",
                        userService.getUserProfileByEmail(email)
                )
        );
    }
}
