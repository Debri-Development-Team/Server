package com.example.debriserver.core.Curri.Model;

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
    private String curriDesc;
    private String visibleStatus;
    private String langTag;
    private Float progressRate;
    private String status;
    private int createdAt;
}
