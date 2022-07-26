package com.example.debriserver.core.Lecture;

import com.example.debriserver.utility.jwtUtility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/lecture")
public class LectureController {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private final LectureProvider lectureProvider;

    @Autowired
    private final LectureService lectureService;

    @Autowired
    private final jwtUtility jwt;


    public LectureController(LectureProvider lectureProvider, LectureService lectureService, jwtUtility JwtUtility)
    {
        this.lectureProvider = lectureProvider;
        this.lectureService = lectureService;
        this.jwt = JwtUtility;
    }













}
