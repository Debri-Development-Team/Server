package com.example.debriserver.core.Lecture;

import com.example.debriserver.basicModels.BasicException;
import com.example.debriserver.basicModels.BasicResponse;
import com.example.debriserver.basicModels.BasicServerStatus;
import com.example.debriserver.core.Lecture.Model.*;
import com.example.debriserver.utility.jwtUtility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    /**
     * 7.1 전체 강의 리스트 조회 API
     * [GET] 127.0.0.1/api/lecture/getLectureList
     * */
    @GetMapping("/getLectureList")
    public BasicResponse<List<GetLectureListRes>> getLectureList(){
        try{
            String jwtToken = jwt.getJwt();

            if(jwt.isJwtExpired(jwtToken)) throw new BasicException(BasicServerStatus.EXPIRED_TOKEN);

            int userIdx = jwt.getUserIdx(jwtToken);

            return new BasicResponse<>(lectureService.getLectureList(userIdx));

        }catch (BasicException exception){
            return new BasicResponse<>((exception.getStatus()));
        }
    }

    /**
     * 7.2 강의 즐겨찾기 API
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
     * 7.2.1 강의 즐겨찾기 삭제 API
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

    /**
     * 7.3 즐겨찾기 한 강의 리스트 조회
     * [GET] 127.0.0.1:8521/api/lecture/getScrapList/{userIdx}
     * */
    @GetMapping("/getScrapList/{userIdx}")
    public BasicResponse<List<GetLectureScrapListRes>>getScrapLectureList(@PathVariable int userIdx){

        try{
            String jwtToken = jwt.getJwt();

            if(jwt.isJwtExpired(jwtToken)) throw new BasicException(BasicServerStatus.EXPIRED_TOKEN);

            if(!lectureProvider.checkScrapActiveLectureExist(userIdx)) throw new BasicException(BasicServerStatus.SCRAP_ACTIVE_NOT_EXIST);

            return new BasicResponse<>(lectureService.getScrapLectureList(userIdx));

        }catch (BasicException exception){
            return new BasicResponse<>((exception.getStatus()));
        }
    }

    /*
     * 7.4 강의 상세조회 API
     * [GET] 127.0.0.1:8521/api/lecture/getLecture/{lectureIdx}
     * */
    @GetMapping("/getLecture/{lectureIdx}")
    public BasicResponse<GetLectureRes> getLecture(@PathVariable int lectureIdx){
        try{
            String jwtToken = jwt.getJwt();

            if(jwt.isJwtExpired(jwtToken)) throw new BasicException(BasicServerStatus.EXPIRED_TOKEN);

            if(!lectureProvider.checkLectureExist(lectureIdx)) throw new BasicException(BasicServerStatus.LECTURE_NOT_EXIST);

            int userIdx = jwt.getUserIdx(jwtToken);

            return new BasicResponse<>(lectureService.getLecture(lectureIdx, userIdx));
        }catch (BasicException exception){
            return new BasicResponse<>((exception.getStatus()));
        }
    }

    /*
     * 검색한 타입에 맞는 강의의 리스트를 리턴
     * 7.4.1 강의 검색 API
     * [GET] 127.0.0.1:8521/api/lecture/search
     * */
    @GetMapping("/search")
    public BasicResponse<List<GetLectureSearchListRes>> searchLecture(@RequestParam("lang") String langTag, @RequestParam("type") String typeTag, @RequestParam("price") String pricing, @RequestParam("key") String keyword){
        try{
            String jwtToken = jwt.getJwt();

            if(jwt.isJwtExpired(jwtToken)) throw new BasicException(BasicServerStatus.EXPIRED_TOKEN);

            int userIdx = jwt.getUserIdx(jwtToken);

            return new BasicResponse<>(lectureService.searchLecture(langTag, typeTag, pricing, keyword, userIdx));

        }catch (BasicException exception){
            return new BasicResponse<>((exception.getStatus()));
        }
    }

    /**
     * 7.6 로드맵 전체 리스트 조회 api
     * */
    @GetMapping("/roadmap/list")
    public BasicResponse<List<GetRoadmapListRes>> getRoadmapList(){
        try{
            String jwtToken = jwt.getJwt();

            if(jwt.isJwtExpired(jwtToken)) throw new BasicException(BasicServerStatus.EXPIRED_TOKEN);

            List<GetRoadmapListRes> getRoadmapListRes = lectureService.getRoadmapList();

            return new BasicResponse<>(getRoadmapListRes);

        }catch (BasicException exception){
            return new BasicResponse<>(exception.getStatus());
        }
    }

    /**
     * 7.6.2 로드맵 상세 조회 api
     * */
    @GetMapping("/roadmap/view")
    public BasicResponse<List<GetRoadmapRes>> getRoadmapView(@RequestParam String mod){
        try{
            String jwtToken = jwt.getJwt();

            if(jwt.isJwtExpired(jwtToken)) throw new BasicException(BasicServerStatus.EXPIRED_TOKEN);

            int userIdx = jwt.getUserIdx(jwtToken);

            List<GetRoadmapRes> getRoadmapRes = lectureService.getRoadmapView(mod, userIdx);

            return new BasicResponse<>(getRoadmapRes);
        }catch (BasicException exception){
            return new BasicResponse<>(exception.getStatus());
        }
    }

    /**
     * 7.6 강의 리뷰 작성 api
     * POST	api/lecture/review/create
     * */
    @PostMapping("/review/create")
    public BasicResponse<LectureReviewRes> createLectureReview(@RequestBody PostLectureReviewReq postLectureReviewReq){
        try{
            String jwtToken = jwt.getJwt();

            if(jwt.isJwtExpired(jwtToken)) throw new BasicException(BasicServerStatus.EXPIRED_TOKEN);

            int authorIdx = jwt.getUserIdx(jwtToken);
            int lectureIdx = postLectureReviewReq.getLectureIdx();
            String authorName = postLectureReviewReq.getAuthorName();
            String content = postLectureReviewReq.getContent();

            return new BasicResponse<>(lectureService.createLectureReview(lectureIdx, authorIdx, authorName, content));

        }catch (BasicException exception){
            return new BasicResponse<>((exception.getStatus()));
        }
    }
    /**
     * 7.6.1 강의 리뷰 조회 api
     * GET	api/lecture/review/get
     * */
    @GetMapping("/review/get")
    public BasicResponse<List<LectureReviewRes>> getLectureReviewList(@RequestParam int lectureIdx) {
        try{
            String jwtToken = jwt.getJwt();

            if(jwt.isJwtExpired(jwtToken)) throw new BasicException(BasicServerStatus.EXPIRED_TOKEN);

            return new BasicResponse<>(lectureService.getLectureReviewList(lectureIdx));
        }catch (BasicException exception){
            return new BasicResponse<>((exception.getStatus()));
        }
    }

    /**
     * 7.7 강의 좋아요 api
     * POST	api/lecture/like/create
     * */
    @PostMapping("/like/create")
    public BasicResponse<LectureLikeRes> createLectureLike(@RequestParam int lectureIdx){
        try{
            String jwtToken = jwt.getJwt();

            if(jwt.isJwtExpired(jwtToken)) throw new BasicException(BasicServerStatus.EXPIRED_TOKEN);

            int userIdx = jwt.getUserIdx(jwtToken);

            if(lectureService.lectureExist(lectureIdx)) throw new BasicException(BasicServerStatus.LECTURE_NOT_EXIST);

            return new BasicResponse<>(lectureService.createLectureLike(lectureIdx, userIdx));
        }catch (BasicException exception){
            return new BasicResponse<>((exception.getStatus()));
        }
    }
    /**
     * 7.7.1 강의 좋아요 삭제 api
     * PATCH	api/lecture/like/delete
     * */

    @PatchMapping("/like/delete")
    public BasicResponse<LectureLikeRes> deleteLectureLike(@RequestParam int lectureIdx){
        try{
            String jwtToken = jwt.getJwt();

            if(jwt.isJwtExpired(jwtToken)) throw new BasicException(BasicServerStatus.EXPIRED_TOKEN);

            int userIdx = jwt.getUserIdx(jwtToken);

            if(lectureService.lectureExist(lectureIdx)) throw new BasicException(BasicServerStatus.LECTURE_NOT_EXIST);

            return new BasicResponse<>(lectureService.deleteLectureLike(lectureIdx, userIdx));
        }catch (BasicException exception){
            return new BasicResponse<>((exception.getStatus()));
        }
    }

    /**
     * 7.5.2 로드맵 복사
     * POST api/lecture/roadmap/copy
     * */
    @PostMapping("/roadmap/copy")
    public BasicResponse<PostRoadmapCopyRes> copyRoadmap(@RequestBody PostRoadmapCopyReq postRoadmapCopyReq) {

        try{
            String jwtToken = jwt.getJwt();

            if(jwt.isJwtExpired(jwtToken)) throw new BasicException(BasicServerStatus.EXPIRED_TOKEN);

            int userIdx = jwt.getUserIdx(jwtToken);

            return new BasicResponse<>(lectureService.copyRoadmap(postRoadmapCopyReq, userIdx));

        }catch (BasicException exception){
            return new BasicResponse<>((exception.getStatus()));
        }
    }
}
