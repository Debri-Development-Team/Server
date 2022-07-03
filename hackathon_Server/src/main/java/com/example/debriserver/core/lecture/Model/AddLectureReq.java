package com.example.debriserver.core.lecture.Model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class AddLectureReq {
    private String lectureName;
    private String lectureDescription;
    private String language;
    private int difficulty;
    private String lectureKind;
    private String pricing;
    private String materialUrl;
    private String imgUrl;
}
