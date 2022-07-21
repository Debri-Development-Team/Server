package com.example.debriserver.core.User.Model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PostSignUpRes {
    private int userIdx;
    private String userId;
    private String nickname;


}

