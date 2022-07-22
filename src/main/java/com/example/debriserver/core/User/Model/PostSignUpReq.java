package com.example.debriserver.core.User.Model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor

public class PostSignUpReq {
    private String userId;
    private String password;
    private String password2;
    private String nickname;
    private String birthday;
}



