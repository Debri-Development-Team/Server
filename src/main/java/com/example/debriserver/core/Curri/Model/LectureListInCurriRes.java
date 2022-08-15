package com.example.debriserver.core.Curri.Model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LectureListInCurriRes {
    private int lectureIdx;
    private String lectureName;
    private String langTag;
    private int chNumber;
    private Float progressRate;
}
