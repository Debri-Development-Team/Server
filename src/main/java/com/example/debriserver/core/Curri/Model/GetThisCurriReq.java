package com.example.debriserver.core.Curri.Model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GetThisCurriReq {
    private int curriIdx;
    private String curriName;
    private String curriAuthor;
    private String visibleStatus;
    private String langTag;
    private String progressRate;
    private int createdAt;
    private String status;
}
