package com.example.debriserver.core.Lecture.Model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ChListRes {
    private int chIdx;
    private int lectureIdx;
    private String chName;
    private int chOrder;
}
