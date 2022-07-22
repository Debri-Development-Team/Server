package com.example.debriserver.core.Jwt;

import com.example.debriserver.basicModels.BasicException;
import com.example.debriserver.basicModels.BasicResponse;
import com.example.debriserver.core.Jwt.Model.PatchRefreshReq;
import com.example.debriserver.core.Jwt.Model.PatchRefreshRes;
import com.example.debriserver.utility.Model.RefreshJwtRes;
import com.example.debriserver.utility.jwtUtility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/jwt")
public class JwtController {

    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private final JwtService jwtService;

    @Autowired
    private final jwtUtility JwtUtility;


    public JwtController(JwtService jwtService, jwtUtility jwtUtility)
    {
        this.jwtService = jwtService;
        this.JwtUtility = jwtUtility;
    }

    @PatchMapping("/refresh")
    public BasicResponse<PatchRefreshRes> getRefresh(@RequestBody PatchRefreshReq patchRefreshReq){
        try{
            RefreshJwtRes refreshJwtRes = JwtUtility.refreshToken(JwtUtility.getJwt(), patchRefreshReq.getRefreshToken());

            PatchRefreshRes patchRefreshRes = jwtService.getRefresh(refreshJwtRes);

            return new BasicResponse<>(patchRefreshRes);

        }catch(BasicException exception){
            return new BasicResponse<>((exception.getStatus()));
        }
    }

}
