package com.example.debriserver.core.Lecture.Model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class GetRoadmapChildRes {
    private int roadmapIdx;
    private int childCurriIdx;
    private int childOrder;
    private String childExplain;
    private String childName;
    private List<GetRoadmapChildLectureList> roadmapChildLectureList;


}
