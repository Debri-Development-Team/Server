package com.example.debriserver.core.Auth.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PostAuthEmailRes {
    private int authNumber;
    private int timeout;
}
