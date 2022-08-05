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

            int userIdx = jwt.getUserIdx();

            PostCurriCreateRes postCurriCreateRes = curriService.createCurri(postCurriCreateReq, userIdx);

            return new BasicResponse<>(postCurriCreateRes);
        } catch (BasicException exception){
            return new BasicResponse<>((exception.getStatus()));
        }
    }


    /**
    *   커리큘럼 수정 API
    * */


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

            int userIdx = jwt.getUserIdx();

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

            int userIdx = jwt.getUserIdx();

            GetThisCurriRes getThisCurriRes = curriService.getThisCurri(getThisCurriReq, userIdx);

            return new BasicResponse<>(getThisCurriRes);
        } catch (BasicException exception) {
            return new BasicResponse<>((exception.getStatus()));
        }
    }

    /*
    *   커리큘럼 삭제 API
    *   [PATCH]: localhost:8521/api/curri/{curriIdx}/delete
    * */
    @ResponseBody
    @PatchMapping("/{curriIdx}/delete")
    public BasicResponse<String> deleteCurri(@PathVariable ("curriIdx") int curriIdx) {
        try {
            String jwtToken = jwt.getJwt();

            if (jwt.isJwtExpired(jwtToken)) throw new BasicException(BasicServerStatus.EXPIRED_TOKEN);

            int userIdx = jwt.getUserIdx();

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
    @PatchMapping("/chapter/complete")
    public BasicResponse<String> chapterStatus(@RequestBody PatchChapterStatuReq patchChapterCompleteReq) {
        try{
            String jwtToken = jwt.getJwt();

            if(jwt.isJwtExpired(jwtToken)) throw new BasicException(BasicServerStatus.EXPIRED_TOKEN);

            // 해당 쳅터가 현재 커리에 들어있는지 확인
            if(curriProvider.checkChapterExist(patchChapterCompleteReq)) throw new BasicException(CURRI_EMPTY_CHAPTER);

            int userIdx = jwt.getUserIdx();

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

}
