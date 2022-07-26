package com.example.debriserver.core.Lecture;

import com.example.debriserver.basicModels.BasicException;
import com.example.debriserver.basicModels.BasicResponse;
import com.example.debriserver.basicModels.BasicServerStatus;
import com.example.debriserver.core.Lecture.Model.PostScrapReq;
import com.example.debriserver.utility.jwtUtility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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
    //@GetMapping("/getLectureList")

    /**
     * 6.2 강의 즐겨찾기 API
     * [POST] 127.0.0.1:8521/api/lecture/scrap/create
     * */
    @PostMapping("/scrap/create")
    public BasicResponse<String> createLectureScrap(@RequestBody PostScrapReq postScrapReq){

        try{
            String jwtToken = jwt.getJwt();

            if(jwt.isJwtExpired(jwtToken)) throw new BasicException(BasicServerStatus.EXPIRED_TOKEN);

            //대상 강의가 존재?
            if(!lectureProvider.checkLectureExist(postScrapReq.getLectureIdx())) throw new BasicException(BasicServerStatus.SCRAP_TARGET_LECTURE_NOT_EXIST);
            //이미 스크랩 했나?
            if(lectureProvider.checkLectureScrapExist(postScrapReq.getUserIdx(), postScrapReq.getLectureIdx())) throw  new BasicException(BasicServerStatus.ALREADY_SCRAP_LECTUER);
            // 스크랩 데이터가 존재하는지 스크랩 했던 안했던
            boolean checkPrarmeter = lectureProvider.checkLectureScrapDataExist(postScrapReq.getUserIdx(), postScrapReq.getLectureIdx());

            boolean result = lectureService.createLectureScrap(postScrapReq.getUserIdx(), postScrapReq.getLectureIdx(), checkPrarmeter);

            if(result) return new BasicResponse<>("스크랩 성공");
            else throw new BasicException(BasicServerStatus.SCRAP_FAIL);

        }catch (BasicException exception){
            return new BasicResponse<>((exception.getStatus()));
        }
    }

    /**
     * 6.3 강의 즐겨찾기 삭제 API
     * [POST] 127.0.0.1:8521/api/lecture/scrap/delete
     * */
    @PatchMapping("/scrap/delete")
    public BasicResponse<String> deleteLectureScrap(@RequestBody PostScrapReq postScrapReq){
        try{
            String jwtToken = jwt.getJwt();

            if(jwt.isJwtExpired(jwtToken)) throw new BasicException(BasicServerStatus.EXPIRED_TOKEN);

            //대상 강의가 존재?
            if(!lectureProvider.checkLectureExist(postScrapReq.getLectureIdx())) throw new BasicException(BasicServerStatus.SCRAP_TARGET_LECTURE_NOT_EXIST);
            //이미 스크랩 삭제함?
            if(lectureProvider.checkLectureUnscrapExist(postScrapReq.getUserIdx(), postScrapReq.getLectureIdx())) throw  new BasicException(BasicServerStatus.ALREADY_UNSCRAP_LECTUER);

            boolean result = lectureService.deleteLectureScrap(postScrapReq.getUserIdx(), postScrapReq.getLectureIdx());

            if(result) return new BasicResponse<>("스크랩 삭제 성공");
            else throw new BasicException(BasicServerStatus.SCRAP_DELETE_FAIL);

        }catch (BasicException exception){
            return new BasicResponse<>((exception.getStatus()));
        }
    }

}
