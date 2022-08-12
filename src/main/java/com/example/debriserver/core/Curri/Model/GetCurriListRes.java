package com.example.debriserver.core.Curri.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GetCurriListRes {
    private int curriIdx;
    private String curriName;
    private String curriAuthor;
    private String visibleStatus;
    private String langTag;
    private Float progressRate;
    private String status;
    private int createdAt;
}
