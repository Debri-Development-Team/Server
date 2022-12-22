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
public class GetLectureListPageRes {
    private List<GetLectureListRes> lectureList;
    private int lectureCount;
}
