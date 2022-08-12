package com.example.debriserver.core.Curri;

import com.example.debriserver.basicModels.BasicException;
import com.example.debriserver.basicModels.BasicResponse;
import com.example.debriserver.basicModels.BasicServerStatus;
import com.example.debriserver.core.Curri.model.*;
import com.example.debriserver.utility.jwtUtility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import static com.example.debriserver.basicModels.BasicServerStatus.*;

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
    *  커리큘럼 제작 API
    *   - 1. Lecture 테이블에서 강의자료를 선택
    *   - 2. 선택한 자료를 Curri_Lecture(이하 CL)에 저장
    *   - 3. CL에 저장한 자료를 한가지 CurriIdx에 매칭
    *   - 받아야 할 값 : 선택된 Lecture에 대한 정보
    *   - 반환할 값 : 성공 여부 및 생성된 CurriIdx
    *   - 저장 할 값 : Curriculum 테이블 꽉꽉
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
    *   커리큘럼 수정 API
    *   [POST]: localhost:8521/api/curri/modify
    * */
    @ResponseBody
    @PostMapping("/modify")
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
     *   강의자료 추가 API
     *   [POST]: localhost:8521/api/curri/insertLecture
     * */
    @ResponseBody
    @PostMapping("/insertLecture")
    public BasicResponse<String> insertLecture(@RequestBody PostInsertLectureReq postInsertLectureReq){
        try{
            String jwtToken = jwt.getJwt();
            if(jwt.isJwtExpired(jwtToken)) throw new BasicException(BasicServerStatus.EXPIRED_TOKEN);

            int userIdx = jwt.getUserIdx(jwtToken);

            boolean check = curriService.insertLecture(postInsertLectureReq, userIdx);

            if (!check) throw new BasicException(BasicServerStatus.CURRI_INSERT_FATL);

            String result = "강의자료가 성공적으로 추가 되었습니다.";

            return new BasicResponse<>(result);
        } catch (BasicException exception){
            return new BasicResponse<>((exception.getStatus()));
        }
    }

    /*
    *   커리큘럼 리스트 조회 API
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
    *   커리큘럼 상세 조회 API
    *   [GET]: localhost:8521/api/curri/getThisCurri
    * */
    @ResponseBody
    @GetMapping("/getThisCurri")
    public BasicResponse<GetThisCurriRes> getThisCurri(@RequestBody GetThisCurriReq getThisCurriReq){
        try {
            String jwtToken = jwt.getJwt();

            if (jwt.isJwtExpired(jwtToken)) throw new BasicException(BasicServerStatus.EXPIRED_TOKEN);

            int userIdx = jwt.getUserIdx(jwtToken);

            GetThisCurriRes getThisCurriRes = curriService.getThisCurri(getThisCurriReq, userIdx);

            return new BasicResponse<>(getThisCurriRes);
        } catch (BasicException exception) {
            return new BasicResponse<>((exception.getStatus()));
        }
    }

    /*
    *   커리큘럼 삭제 API
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
    *   챕터 완료 및 취소 API
    *   [PATCH]: localhost:8521/api/curri/chapter/status
    * */
    @ResponseBody
    @PatchMapping("/chapter/status")
    public BasicResponse<String> chapterStatus(@RequestBody PatchChapterStatuReq patchChapterCompleteReq) {
        try{
            String jwtToken = jwt.getJwt();

            if(jwt.isJwtExpired(jwtToken)) throw new BasicException(BasicServerStatus.EXPIRED_TOKEN);

            // 해당 쳅터가 현재 커리에 들어있는지 확인
            if(curriProvider.checkChapterExist(patchChapterCompleteReq)) throw new BasicException(CURRI_EMPTY_CHAPTER);

            int userIdx = jwt.getUserIdx(jwtToken);

            String result;
            // 챕터 완료인지 취소인지 확인
            if(curriProvider.checkChapterStatus(patchChapterCompleteReq)){
                curriService.completeChapter(patchChapterCompleteReq, userIdx);
                result = "칭찬도장 꾸-욱!";
            } else{
                curriService.cancelCompleteChapter(patchChapterCompleteReq, userIdx);
                result = "넌 항상 이런식이야";
            }

            return new BasicResponse<>(result);
        } catch (BasicException exception){
            return new BasicResponse<>((exception.getStatus()));
        }
    }

    /**
     * 커리큘럼 스크랩 API
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
     * 커리큘럼 스크랩 취소 API
     */

    @ResponseBody
    @PatchMapping("/scrap/cancel/{curriIdx}")
    public BasicResponse<String> scrapCancel(@PathVariable("curriIdx") int curriIdx) {
        try {
            String jwtToken = jwt.getJwt();
            if (jwt.isJwtExpired(jwtToken)) throw new BasicException(BasicServerStatus.EXPIRED_TOKEN);

            int userIdx = jwt.getUserIdx(jwtToken);

            if(curriService.checkScrapedCurriExist(curriIdx,userIdx)== true) throw new BasicException(BasicServerStatus.SCRAP_Curri_EXIST);


            String result = "스크랩이 취소 되었습니다.";


            return new BasicResponse<>(result);

        } catch (BasicException exception) {
            return new BasicResponse<>((exception.getStatus()));
        }
    }
    /**
     * 스크랩한 커리큘럼 리스트 조회 API
     */

    /**
     * 스크랩한 커리큘럼 상세조회 API
     */
}
