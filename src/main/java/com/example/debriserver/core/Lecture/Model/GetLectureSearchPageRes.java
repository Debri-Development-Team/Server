package com.example.debriserver.core.Lecture.Model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GetLectureSearchPageRes {
    private List<GetLectureSearchListRes> lectureList;
    private int lectureCount;
}
