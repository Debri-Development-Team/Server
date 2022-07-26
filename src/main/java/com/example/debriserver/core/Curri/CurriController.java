package com.example.debriserver.core.Curri;

import com.example.debriserver.core.Curri.CurriProvider;
import com.example.debriserver.core.Curri.CurriService;
import com.example.debriserver.utility.jwtUtility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

}
