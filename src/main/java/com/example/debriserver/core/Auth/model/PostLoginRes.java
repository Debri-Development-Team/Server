package com.example.debriserver.core.Auth.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class PostLoginRes {
    private int userIdx;
    private String userName;
    private String userID;
    private String userBirthday;
    private String jwt;
    private String refreshToken;
    private boolean firstLogin;
}
