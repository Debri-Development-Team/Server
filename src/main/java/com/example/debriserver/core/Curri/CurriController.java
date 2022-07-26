package com.example.debriserver.core.Curri;

import com.example.debriserver.core.Curri.CurriProvider;
import com.example.debriserver.core.Curri.CurriService;
import com.example.debriserver.utility.jwtUtility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
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
    *   - 1. Lecture 테이블에서 강의자료를 선택
    *   - 2. 선택한 자료를 Curri_Lecture(이하 CL)에 저장
    *   - 3. CL에 저장한 자료를 한가지 CurriIdx에 매칭
    *   - 받아야 할 값 : 선택된 Lecture에 대한 정보
    *   - 반환할 값 : 성공 여부 및 생성된 CurriIdx
    *   - 저장 할 값 : Curriculum 테이블 꽉꽉
    *   [POST]: localhost:8521/api/curri/create
    */

    @PostMapping("/create")
    public


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
