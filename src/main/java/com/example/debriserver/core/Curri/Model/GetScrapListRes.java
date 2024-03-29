package com.example.debriserver.core.Curri.Model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor

public class GetScrapListRes {

    private int curriIdx;
    private String curriName;
    private String curriAuthor;
    private String langTag;
    private float progressRate;


}
