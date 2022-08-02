package com.example.debriserver.core.Curri;

import com.example.debriserver.basicModels.BasicException;
import com.example.debriserver.basicModels.BasicResponse;
import com.example.debriserver.basicModels.BasicServerStatus;
import com.example.debriserver.core.Curri.model.PostCurriScrapRes;
import com.example.debriserver.utility.jwtUtility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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
    *   /api/curri/creat
    * */

    /*
    *   커리큘럼 수정 API
    * */

    /*
    *   커리큘럼 리스트 조회 API
    * */

    /*
    *   커리큘럼 상세 조회 API
    * */

    /*
    *   커리큘럼 삭제 API
    * */

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
