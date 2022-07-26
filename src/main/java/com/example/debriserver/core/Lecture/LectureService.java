package com.example.debriserver.core.Lecture;

import com.example.debriserver.utility.jwtUtility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LectureService {
    @Autowired
    private final jwtUtility jwt;

    @Autowired
    private final LectureProvider lectureProvider;

    @Autowired
    private final LectureDao lectureDao;

    public LectureService(jwtUtility jwt, LectureProvider lectureProvider, LectureDao lectureDao){
        this.jwt = jwt;
        this.lectureProvider = lectureProvider;
        this.lectureDao = lectureDao;
    }

    public String createLectureScrap(int userIdx, int lectureIdx) {
    }
}
