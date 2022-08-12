package com.example.debriserver.core.Curri.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ChapterListInCurriRes {
    private int chIdx;
    private String chName;
    private int chNumber;
    private String langTag;
    private String chComplete;
    private int progressOrder;
    private int completeChNumber;
}
