package com.example.debriserver.core.user.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class User {

    private int userIdx;
    private String id;
    private String password;
    private String nickName;
    private String birthday;

}
