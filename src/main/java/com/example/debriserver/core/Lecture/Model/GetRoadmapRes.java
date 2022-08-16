package com.example.debriserver.core.Lecture.Model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class GetRoadmapRes {

    private int roadmapIdx;
    private String roadmapName;
    private String roadmapExplain;
    private String authorName;
    private int requireDay;
    List<GetRoadmapChildRes> roadmapChildCurriList;
}
