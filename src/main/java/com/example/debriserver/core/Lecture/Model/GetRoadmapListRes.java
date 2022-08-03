package com.example.debriserver.core.Lecture.Model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GetRoadmapListRes {

    private int roadmapIdx;
    private String roadmapName;
    private String langTag;
    private int curriNumber;
    private String status;
}
