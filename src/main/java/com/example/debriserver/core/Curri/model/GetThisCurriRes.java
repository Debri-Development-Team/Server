package com.example.debriserver.core.Curri.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GetThisCurriRes {
    private int curriIdx;
    private String curriName;
    private String visibleStatus;
    private String langTag;
    private Float progressRate;
    private String status;
    private int completeAt;
    private int dDay;
    private Timestamp createdAt;

    List<LectureListInCurriRes> lectureListResList;

    List<ChapterListInCurriRes> chapterListResList;
}
