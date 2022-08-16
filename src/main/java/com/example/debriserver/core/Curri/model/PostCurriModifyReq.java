package com.example.debriserver.core.Curri.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PostCurriModifyReq {

    private int curriIdx;
    private String curriName;
    private String visibleStatus;
    private String status;

}
