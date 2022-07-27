package com.example.debriserver.core.Curri.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor

public class CurriScrapRes {

    private int curriIdx;
    private String curriName;
    private String curriAuthor;
    private String visibleStatus;
    private String langTag;
    private float progressRate;
    private int createdAt;
    private int updatedAt;
    private String status;
    private String scraped;
    private int ownerIdx;


}
