package com.example.debriserver.core.Lecture;

import com.example.debriserver.utility.jwtUtility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LectureProvider {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private final jwtUtility jwt;

    @Autowired
    private final LectureDao lectureDao;

    public LectureProvider(jwtUtility jwt, LectureDao lectureDao){
        this.jwt = jwt;
        this.lectureDao = lectureDao;
    }


}
