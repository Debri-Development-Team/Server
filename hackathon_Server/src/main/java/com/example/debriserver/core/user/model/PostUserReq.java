package com.example.debriserver.core.User.Model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class PostUserReq {

    private String id;
    private String password;
    private String nickname;
    private String birthday;

}
