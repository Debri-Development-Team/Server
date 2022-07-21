package com.example.debriserver.core.Jwt.Model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PatchRefreshRes {
    private String accessToken;
    private String refreshToken;
}
