package com.example.debriserver.core.lecture.Model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class SearchLectureReq {
    private String language = null;
    private int difficulty = -1;
    private String lectureKind = null;
    private String lectureName;
}
