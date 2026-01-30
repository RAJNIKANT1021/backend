package com.example.demo.controller;
import com.example.demo.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class UserController {
    private final UserService userservice;
    public UserController(UserService userService){
        this.userservice=userService;
    }

    // 🔐 Protected GET API
    @GetMapping("/profile")
    public ResponseEntity<?> getProfile(Authentication authentication) {

        // ✅ Defensive check (prevents 500 error)
        if (authentication == null) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body("Unauthorized");
        }

        // authentication.getName() = email (set in JwtFilter)
        String Email=authentication.getName();
      return ResponseEntity.ok(userservice.getUserProfileByEmail(Email));
    }
}
