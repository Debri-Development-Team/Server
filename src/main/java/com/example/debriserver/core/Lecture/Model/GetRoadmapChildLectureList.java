package com.example.debriserver.core.Lecture.Model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class GetRoadmapChildLectureList {
    private int PCurriIdx;
    private int childLectureIdx;
    private String childLectureName;
    private int childChapterNumber;
    private String childLangTag;
    private String childPricing;
    private String childMaterialType;
    private boolean userScrap;
    private int scrapNumber;
    private int usedCount;
    private int likeNumber;
    private boolean userLike;
}
