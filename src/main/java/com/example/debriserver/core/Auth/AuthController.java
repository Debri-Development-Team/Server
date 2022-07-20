package com.example.debriserver.core.Auth;

import com.example.debriserver.basicModels.BasicException;
import com.example.debriserver.basicModels.BasicResponse;
import com.example.debriserver.basicModels.BasicServerStatus;
import com.example.debriserver.core.Auth.model.PostLoginReq;
import com.example.debriserver.core.Auth.model.PostLoginRes;
import com.example.debriserver.utility.jwtUtility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import static com.example.debriserver.utility.ValidationRegex.isRegexEmail;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private final AuthProvider authProvider;

    @Autowired
    private final AuthService authService;

    @Autowired
    private final jwtUtility JwtUtility;


    public AuthController(AuthProvider authProvider, AuthService authService, jwtUtility JwtUtility)
    {
        this.authProvider = authProvider;
        this.authService = authService;
        this.JwtUtility = JwtUtility;
    }

    @ResponseBody
    @PostMapping("/login")
    public BasicResponse<PostLoginRes> login(@RequestBody PostLoginReq postLoginReq)
    {
        try{
            // 이메일 미입력시
            if(postLoginReq.getEmail().equals(""))
            {
                return new BasicResponse<>(BasicServerStatus.POST_USERS_EMPTY_EMAIL);
            }

            // 이메일 형식이 잘못된 경우
            if(!isRegexEmail(postLoginReq.getEmail()))
            {
                return new BasicResponse<>(BasicServerStatus.POST_USERS_INVALID_EMAIL);
            }

            // 비밀번호 미입력시
            if(postLoginReq.getPwd().equals(""))
            {
                return new BasicResponse<>(BasicServerStatus.POST_USERS_EMPTY_PASSWORD);
            }

            PostLoginRes postLoginRes = authService.login(postLoginReq);
            return new BasicResponse<>(postLoginRes);

        }catch (BasicException exception)
        {
            return new BasicResponse<>((exception.getStatus()));
        }
    }

}
