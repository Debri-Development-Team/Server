package com.example.debriserver.core.Curri.Model;

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
    private int lectureIdx;
    private int curriIdx;
    private String chName;
    private int chNumber;
    private String langTag;
    private String chComplete;
    private int progressOrder;
    private int completeChNumber;

}
