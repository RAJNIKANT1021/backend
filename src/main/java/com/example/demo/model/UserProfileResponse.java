package com.example.demo.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserProfileResponse {
    private String id;
    private String name;
    private String email;

    public UserProfileResponse(String id , String name ,String email){
        this.email=email;
        this.name=name;
        this.id=id;
    }
}
