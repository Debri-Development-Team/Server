package com.example.debriserver.core.Lecture.Model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class GetLectureRes {
    private int lectureIdx;
    private String lectureName;
    private String lectureDesc;
    private String langTag;
    private String pricing;
    private String srcLink;
    private String materialType;
    private int chapterNumber;
    private int usedCount;
    private int likeNumber;
    private boolean userLike;
    private boolean userScrap;
    List<ChListRes> chapterList;
}
