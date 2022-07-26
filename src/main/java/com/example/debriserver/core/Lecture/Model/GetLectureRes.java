package com.example.debriserver.core.Lecture.Model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class GetLectureRes {
    private int lectureIdx;
    private String lectureName;
    private String lectureDescription;
    private String langTag;
    private int materialNumber;
    private String pricing;
    private String createdAt;
    private String updatedAt;
    private String complete;
    private int materialIdx;
    private String materialName;
    private String materialAuthor;
    private String materialPublisher;
    private String materialPublishDate;
    private String materialLink;
    private int chapterNumber;
}
