package com.example.debriserver.core.Lecture.Model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class GetLectureSearchListRes {
    private int lectureIdx;
    private String lectureName;
    private int chapterNumber;
    private String langTag;
    private String pricing;
    private String materialType;
    private boolean userScrap;
}
