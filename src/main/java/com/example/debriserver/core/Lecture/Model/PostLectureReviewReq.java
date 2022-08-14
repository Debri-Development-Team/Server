package com.example.debriserver.core.Lecture.Model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class PostLectureReviewReq {
    private int lectureIdx;
    private String authorName;
    private String content;
}
