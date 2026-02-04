package com.example.demo.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserProfileResponse {

    private String id;
    private String name;
    private String email;

    public UserProfileResponse(String id, String name, String email) {
        this.id = id;
        this.name = name;
        this.email = email == null ? null : email.toLowerCase();
    }
}
