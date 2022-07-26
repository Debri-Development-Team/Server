package com.example.debriserver.core.Lecture;

import com.example.debriserver.basicModels.BasicException;
import com.example.debriserver.basicModels.BasicResponse;
import com.example.debriserver.basicModels.BasicServerStatus;
import com.example.debriserver.core.Lecture.Model.PostScrapReq;
import com.example.debriserver.utility.jwtUtility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
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

    @PostMapping
    public BasicResponse<String> createLectureScrap(@RequestBody PostScrapReq postScrapReq){

        try{
            String jwtToken = jwt.getJwt();

            if(jwt.isJwtExpired(jwtToken)) throw new BasicException(BasicServerStatus.EXPIRED_TOKEN);
            //대상 강의가 존재?
            if(!lectureProvider.checkLectureExist(postScrapReq.getLectureIdx())) throw new BasicException(BasicServerStatus.SCRAP_TARGET_LECTURE_NOT_EXIST);

            String result = lectureService.createLectureScrap(postScrapReq.getUserIdx(), postScrapReq.getLectureIdx());

            return new BasicResponse<>(result);

        }catch (BasicException exception){
            return new BasicResponse<>((exception.getStatus()));
        }
    }

}
