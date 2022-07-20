package com.example.debriserver.core.Jwt.Model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class PatchRefreshReq {
    private String expiredToken;
    private String refreshToken;
}
