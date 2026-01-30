package com.example.demo.service;
import com.example.demo.model.User;
import com.example.demo.model.UserProfileResponse;
import com.example.demo.repository.UserRepository;

import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UserProfileResponse getUserProfileByEmail(String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // ✅ Map Entity → DTO (never expose password)
        return new UserProfileResponse(
                user.getId(),
                user.getName(),
                user.getEmail()
        );
    }
}
