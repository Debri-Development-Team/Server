package com.example.debriserver.core.Curri;

import com.example.debriserver.basicModels.BasicException;
import com.example.debriserver.basicModels.BasicResponse;
import com.example.debriserver.basicModels.BasicServerStatus;
import com.example.debriserver.core.Curri.Model.*;
import com.example.debriserver.utility.jwtUtility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import static com.example.debriserver.basicModels.BasicServerStatus.*;

import java.util.List;

@RestController
@RequestMapping("/api/curri")
public class CurriController {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private final CurriProvider curriProvider;

    @Autowired
    private final CurriService curriService;

    @Autowired
    private final jwtUtility jwt;


    public CurriController(CurriProvider curriProvider, CurriService curriService, jwtUtility JwtUtility)
    {
        this.curriProvider = curriProvider;
        this.curriService = curriService;
        this.jwt = JwtUtility;
    }


    /*
    *   8.1 커리큘럼 제작 API
    *   [POST]: localhost:8521/api/curri/create
    */

    @ResponseBody
    @PostMapping("/create")
    public BasicResponse<PostCurriCreateRes> createCurri(@RequestBody PostCurriCreateReq postCurriCreateReq) {
        try{
            // jwtToken 인증
            String jwtToken = jwt.getJwt();
            if(jwt.isJwtExpired(jwtToken)) throw new BasicException(BasicServerStatus.EXPIRED_TOKEN);

            // curri 제목 미입력시
            if(postCurriCreateReq.getCurriName().equals(""))
            {
                throw new BasicException(BasicServerStatus.CURRI_EMPTY_NAME);
            }

            int userIdx = jwt.getUserIdx(jwtToken);

            PostCurriCreateRes postCurriCreateRes = curriService.createCurri(postCurriCreateReq, userIdx);

            return new BasicResponse<>(postCurriCreateRes);
        } catch (BasicException exception){
            return new BasicResponse<>((exception.getStatus()));
        }
    }

    /*
     *   8.2 커리큘럼 리스트 조회 API
     *   [GET]: localhost:8521/api/curri/getList
     */
    @ResponseBody
    @GetMapping("/getList")
    public BasicResponse<List<GetCurriListRes>> getList(){
        try{
            String jwtToken = jwt.getJwt();

            if (jwt.isJwtExpired(jwtToken)) throw new BasicException(BasicServerStatus.EXPIRED_TOKEN);

            int userIdx = jwt.getUserIdx(jwtToken);

            List<GetCurriListRes> getCurriListResList = curriProvider.getList(userIdx);

            return new BasicResponse<>(getCurriListResList);

        } catch (BasicException exception) {
            return new BasicResponse<>((exception.getStatus()));
        }
    }

    /*
     *   8.3 커리큘럼 상세 조회 API
     *   [GET]: localhost:8521/api/curri/getThisCurri
     * */
    @ResponseBody
    @GetMapping("/getThisCurri/{curriIdx}")
    public BasicResponse<GetThisCurriRes> getThisCurri(@PathVariable ("curriIdx") int curriIdx){
        try {
            String jwtToken = jwt.getJwt();

            if (jwt.isJwtExpired(jwtToken)) throw new BasicException(BasicServerStatus.EXPIRED_TOKEN);

            GetThisCurriRes getThisCurriRes = curriService.getThisCurri(curriIdx);

            return new BasicResponse<>(getThisCurriRes);
        } catch (BasicException exception) {
            return new BasicResponse<>((exception.getStatus()));
        }
    }

    /*
    *   8.4 커리큘럼 수정 API
    *   [PATCH]: localhost:8521/api/curri/modify
    * */
    @ResponseBody
    @PatchMapping ("/modify")
    public BasicResponse<String> curriModify(@RequestBody PostCurriModifyReq postCurriModifyReq){
        try{
            // jwtToken 인증
            String jwtToken = jwt.getJwt();
            if(jwt.isJwtExpired(jwtToken)) throw new BasicException(BasicServerStatus.EXPIRED_TOKEN);

            int userIdx = jwt.getUserIdx(jwtToken);

            boolean check = curriService.curriModify(postCurriModifyReq, userIdx);

            if (!check) throw new BasicException(BasicServerStatus.CURRI_MODIFY_FAIL);

            String result = "커리큘럼 수정에 성공하였습니다.";

            return new BasicResponse<>(result);

        } catch (BasicException exception){
            return new BasicResponse<>((exception.getStatus()));
        }
    }

    /*
     *   8.4.1 커리큘럼 제목 수정 API
     *   [PATCH]: localhost:8521/api/curri/modify/name
     * */
    @ResponseBody
    @PatchMapping ("/modify/name")
    public BasicResponse<String> curriNameModify(@RequestBody PacthCurriNameModifyReq pacthCurriNameModifyReq){
        try{
            // jwtToken 인증
            String jwtToken = jwt.getJwt();
            if(jwt.isJwtExpired(jwtToken)) throw new BasicException(BasicServerStatus.EXPIRED_TOKEN);

            int userIdx = jwt.getUserIdx(jwtToken);

            boolean check = curriService.curriNameModify(pacthCurriNameModifyReq, userIdx);

            if (!check) throw new BasicException(BasicServerStatus.CURRI_MODIFY_FAIL);

            String result = "커리큘럼 제목 수정 성공";

            return new BasicResponse<>(result);

        } catch (BasicException exception){
            return new BasicResponse<>((exception.getStatus()));
        }
    }

    /*
     *   8.4.2 커리큘럼 공유 상태 수정 API
     *   [PATCH]: localhost:8521/api/curri/modify/visibleStatus
     * */
    @ResponseBody
    @PatchMapping ("/modify/visibleStatus")
    public BasicResponse<String> curriVisibleStatusModify(@RequestBody PacthCurriVisibleStatusModifyReq pacthCurriVisibleStatusModifyReq){
        try{
            // jwtToken 인증
            String jwtToken = jwt.getJwt();
            if(jwt.isJwtExpired(jwtToken)) throw new BasicException(BasicServerStatus.EXPIRED_TOKEN);

            int userIdx = jwt.getUserIdx(jwtToken);

            boolean check = curriService.curriVisibleStatusModify(pacthCurriVisibleStatusModifyReq, userIdx);

            if (!check) throw new BasicException(BasicServerStatus.CURRI_MODIFY_FAIL);

            String result = "커리큘럼 공유 상태 수정 성공";

            return new BasicResponse<>(result);

        } catch (BasicException exception){
            return new BasicResponse<>((exception.getStatus()));
        }
    }

    /*
     *   8.4.3 커리큘럼 활성 상태 수정 API
     *   [PATCH]: localhost:8521/api/curri/modify/status
     * */
    @ResponseBody
    @PatchMapping ("/modify/status")
    public BasicResponse<String> curriStatusModify(@RequestBody PacthCurriStatusModifyReq pacthCurriStatusModifyReq){
        try{
            // jwtToken 인증
            String jwtToken = jwt.getJwt();
            if(jwt.isJwtExpired(jwtToken)) throw new BasicException(BasicServerStatus.EXPIRED_TOKEN);

            int userIdx = jwt.getUserIdx(jwtToken);

            boolean check = curriService.curriStatusModify(pacthCurriStatusModifyReq, userIdx);

            if (!check) throw new BasicException(BasicServerStatus.CURRI_MODIFY_FAIL);

            String result = "커리큘럼 활성 상태 수정 성공";

            return new BasicResponse<>(result);

        } catch (BasicException exception){
            return new BasicResponse<>((exception.getStatus()));
        }
    }

    /*
     *   8.5 강의자료 추가 API
     *   [POST]: localhost:8521/api/curri/insertLecture
     * */
    @ResponseBody
    @PostMapping("/insertLecture")
    public BasicResponse<String> insertLecture(@RequestBody PostInsertLectureReq postInsertLectureReq){
        try{
            String jwtToken = jwt.getJwt();
            if(jwt.isJwtExpired(jwtToken)) throw new BasicException(BasicServerStatus.EXPIRED_TOKEN);

            int userIdx = jwt.getUserIdx(jwtToken);
            int curriIdx = postInsertLectureReq.getCurriIdx();

            if(!curriProvider.checkCurriExist(curriIdx, userIdx)) throw new BasicException(CURRI_EMPTY_ID);

            boolean check = curriService.insertLecture(postInsertLectureReq, userIdx);

            if (!check) throw new BasicException(BasicServerStatus.CURRI_INSERT_FATL);

            String result = "강의자료가 성공적으로 추가 되었습니다.";

            return new BasicResponse<>(result);
        } catch (BasicException exception){
            return new BasicResponse<>((exception.getStatus()));
        }
    }

    /*
    *   8.6 커리큘럼 삭제 API
    *   [PATCH]: localhost:8521/api/curri/delete/{curriIdx}
    * */
    @ResponseBody
    @PatchMapping("/delete/{curriIdx}")
    public BasicResponse<String> deleteCurri(@PathVariable ("curriIdx") int curriIdx) {
        try {
            String jwtToken = jwt.getJwt();

            if (jwt.isJwtExpired(jwtToken)) throw new BasicException(BasicServerStatus.EXPIRED_TOKEN);

            int userIdx = jwt.getUserIdx(jwtToken);

            curriService.deleteCurri(curriIdx, userIdx);
            String result = "삭제를 성공했습니다.";
            return new BasicResponse<>(result);
        } catch (BasicException exception) {
            return new BasicResponse<>((exception.getStatus()));
        }
    }

    /*
    *   8.7 챕터 완료 및 취소 API
    *   [PATCH]: localhost:8521/api/curri/chapter/status
    * */
    @ResponseBody
    @PatchMapping("/chapter/status")
    public BasicResponse<String> chapterStatus(@RequestBody PatchChapterStatuReq patchChapterCompleteReq) {
        try{
            String jwtToken = jwt.getJwt();

            if(jwt.isJwtExpired(jwtToken)) throw new BasicException(BasicServerStatus.EXPIRED_TOKEN);

            int userIdx = jwt.getUserIdx(jwtToken);

            // 해당 쳅터가 현재 커리에 들어있는지 확인
            if(!curriProvider.checkChapterExist(patchChapterCompleteReq)) throw new BasicException(CURRI_EMPTY_CHAPTER);

            String result;
            // 챕터 완료인지 취소인지 확인
            if(!curriProvider.checkChapterStatus(patchChapterCompleteReq)){
                curriService.completeChapter(patchChapterCompleteReq, userIdx);
                result = "챕터 완료 성공";
            } else{
                curriService.cancelCompleteChapter(patchChapterCompleteReq);
                result = "챕터 완료 취소 성공";
            }

            return new BasicResponse<>(result);
        } catch (BasicException exception){
            return new BasicResponse<>((exception.getStatus()));
        }
    }

    /**
     *  8.8 커리큘럼 좋아요(추천) API
     *  [POST]: localhost:8521/api/curri/scrap/{curriIdx}
     */
    @ResponseBody
    @PostMapping("/scrap/{curriIdx}")
    public BasicResponse<PostCurriScrapRes> scrapCurri(@PathVariable("curriIdx") int curriIdx) {
        try {
            String jwtToken = jwt.getJwt();
            if (jwt.isJwtExpired(jwtToken)) throw new BasicException(BasicServerStatus.EXPIRED_TOKEN);

            int userIdx = jwt.getUserIdx(jwtToken);

            if(curriService.checkScrapedCurriExist(curriIdx,userIdx)== true) throw new BasicException(BasicServerStatus.SCRAP_Curri_EXIST);

            PostCurriScrapRes postCurriScrapRes = curriService.scrapCurri(curriIdx, userIdx);

            return new BasicResponse<>(postCurriScrapRes);

        } catch (BasicException exception) {
            return new BasicResponse<>((exception.getStatus()));
        }
    }

    /**
     *  8.9 커리큘럼 좋아요(추천) 취소 API
     *  [PATCH]: localhost:8521/api/curri/unScrap/{scrapIdx}
     */
    @ResponseBody
    @PatchMapping("/unScrap/{scrapIdx}")
    public BasicResponse<String> scrapCancel(@PathVariable("scrapIdx") int scrapIdx) {
        try {
            String jwtToken = jwt.getJwt();
            if (jwt.isJwtExpired(jwtToken)) throw new BasicException(BasicServerStatus.EXPIRED_TOKEN);

            int userIdx = jwt.getUserIdx(jwtToken);

            if(curriService.checkUnScrapedCurriExist(scrapIdx)== true) throw new BasicException(BasicServerStatus.UNSCRAP_Curri_EXIST);

            curriService.scrapCancel(scrapIdx);
            String result = "스크랩이 취소 되었습니다.";

            return new BasicResponse<>(result);

        } catch (BasicException exception) {
            return new BasicResponse<>((exception.getStatus()));
        }
    }

    /**
     *  8.10 커리큘럼 좋아요(추천) 리스트 조회 API
     *  [GET]: localhost:8521/api/curri/scrap/getList
     */
    @GetMapping("/getScrapList")
    public BasicResponse<List<GetScrapListRes>>getCurriScrapList(){

        try{
            String jwtToken = jwt.getJwt();

            int userIdx = jwt.getUserIdx(jwtToken);

            if(jwt.isJwtExpired(jwtToken)) throw new BasicException(BasicServerStatus.EXPIRED_TOKEN);
            if(curriService.checkScrapExist(userIdx)==false) throw new BasicException(BasicServerStatus.SCRAP_LIST_EMPTY);

            return new BasicResponse<>(curriService.getCurriScrapList(userIdx));

        }catch (BasicException exception){
            return new BasicResponse<>((exception.getStatus()));
        }
    }


    /**
     *  8.10.1 커리큘럼 좋아요(추천) top 10 리스트 조회 API
     *  [GET]: localhost:8521/api/curri/scrap/topList
     */
    @ResponseBody
    @GetMapping("/scrap/topList")
    public BasicResponse<List<GetScrapTopListRes>> getScrapTopList(){
        try{
            String jwtToken = jwt.getJwt();

            if (jwt.isJwtExpired(jwtToken)) throw new BasicException(BasicServerStatus.EXPIRED_TOKEN);

            int userIdx = jwt.getUserIdx(jwtToken);

            List<GetScrapTopListRes> getScrapTopListRes = curriService.getScrapTopList();

            return new BasicResponse<>(getScrapTopListRes);

        } catch (BasicException exception) {
            return new BasicResponse<>((exception.getStatus()));
        }
    }

    /**
     *  8.11 커리큘럼 리셋 API
     *  [PATCH]: localhost:8521/api/curri/reset/{curriIdx}
     */
    @ResponseBody
    @PatchMapping("/reset/{curriIdx}")
    public BasicResponse<String> curriReset(@PathVariable ("curriIdx") int curriIdx){
        try{
            String jwtToken = jwt.getJwt();

            if(jwt.isJwtExpired(jwtToken)) throw new BasicException(BasicServerStatus.EXPIRED_TOKEN);

            int userIdx = jwt.getUserIdx(jwtToken);

            if(!curriService.curriReset(curriIdx, userIdx)) throw new BasicException(CURRI_RESET_FAIL);

            String result = "커리큘럼 리셋 성공";

            return new BasicResponse<>(result);
        } catch (BasicException exception){
            return new BasicResponse<>((exception.getStatus()));
        }
    }

    /**
     *  8.12 커리큘럼 리뷰 작성 API
     *  [POST]: localhost:8521/api/curri/review/create
     */
    @ResponseBody
    @PostMapping("/review/create")
    public BasicResponse<CurriReviewRes> createCurriReview(@RequestBody PostCurriReviewReq postCurriReviewReq){
        try{
            String jwtToken = jwt.getJwt();

            if(jwt.isJwtExpired(jwtToken)) throw new BasicException(BasicServerStatus.EXPIRED_TOKEN);

            int authorIdx = jwt.getUserIdx(jwtToken);

            CurriReviewRes curriReviewRes = curriService.createCurriReview(postCurriReviewReq, authorIdx);

            return new BasicResponse<>(curriReviewRes);
        } catch (BasicException exception){
            return new BasicResponse<>((exception.getStatus()));
        }
    }

    /**
     *  8.12.1 커리큘럼 리뷰 조회 API
     *  [GET]: localhost:8521/api/curri/review/getList
     */
    @ResponseBody
    @GetMapping("/review/getList/{curriIdx}")
    public BasicResponse<List<CurriReviewRes>> getCurriReviewList(@PathVariable ("curriIdx") int curriIdx){
        try{
            String jwtToken = jwt.getJwt();

            if(jwt.isJwtExpired(jwtToken)) throw new BasicException(BasicServerStatus.EXPIRED_TOKEN);

            List<CurriReviewRes> curriReviewList = curriService.getCurriReviewList(curriIdx);

            return new BasicResponse<>(curriReviewList);
        } catch (BasicException exception){
            return new BasicResponse<>((exception.getStatus()));
        }
    }

    /**
     *  8.13 커리큘럼 복붙 API
     *  [POST]: localhost:8521/api/curri/copy
     */
    @ResponseBody
    @PostMapping("/copy")
    public BasicResponse<String> curriCopy(@RequestBody PostCurriCopyReq postCurriCopyReq){
        try{
            String jwtToken = jwt.getJwt();

            if(jwt.isJwtExpired(jwtToken)) throw new BasicException(BasicServerStatus.EXPIRED_TOKEN);

            int userIdx = jwt.getUserIdx(jwtToken);

            if(curriService.curriCopy(postCurriCopyReq, userIdx)) throw new BasicException(CURRI_COPY_FAIL);

            String result = "커리큘럼이 성공적으로 추가 되었습니다";

            return new BasicResponse<>(result);
        } catch (BasicException exception){
            return new BasicResponse<>((exception.getStatus()));
        }
    }

    /**
     *  8.14 최신 커리큘럼 리스트 조회 API
     *  [GET]: localhost:8521/api/curri/getTopList
     */
    @ResponseBody
    @GetMapping("getTopList")
    public BasicResponse<List<GetLatestListRes>> getLatestList(){
        try{
            String jwtToken = jwt.getJwt();

            if(jwt.isJwtExpired(jwtToken)) throw new BasicException(BasicServerStatus.EXPIRED_TOKEN);

            List<GetLatestListRes> getLatestListResList = curriService.getLatestList();

            return new BasicResponse<>(getLatestListResList);
        } catch (BasicException exception){
            return new BasicResponse<>((exception.getStatus()));
        }
    }

}
