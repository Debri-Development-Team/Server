package com.example.debriserver.core.User;

import com.example.debriserver.basicModels.BasicException;
import com.example.debriserver.basicModels.BasicResponse;
import com.example.debriserver.basicModels.BasicServerStatus;
import com.example.debriserver.core.Auth.model.PostLoginRes;
import com.example.debriserver.core.User.Model.PostSignUpReq;
import com.example.debriserver.core.User.Model.PostSignUpRes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import static com.example.debriserver.utility.ValidationRegex.isRegexEmail;


@RestController
@RequestMapping("/api/User")
public class UserController {

    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private final UserService userService;

    @Autowired
    private final UserDao userDao;



    public UserController(UserService userService, UserDao userDao) {
        this.userService = userService;
        this.userDao = userDao;

    }

    @ResponseBody
    @PostMapping("/signUp")
    public BasicResponse<PostSignUpRes> signUp(@RequestBody PostSignUpReq postSignUpReq) {
        try{
            // 이메일 (아이디) 미입력시
            if(postSignUpReq.getUserId().equals(""))
            {
                return new BasicResponse<>(BasicServerStatus.POST_USERS_EMPTY_EMAIL);
            }

            // 이메일(아이디) 형식이 잘못된 경우
            if(!isRegexEmail(postSignUpReq.getUserId()))
            {
                return new BasicResponse<>(BasicServerStatus.POST_USERS_INVALID_EMAIL);
            }

            // 비밀번호 미입력시
            if(postSignUpReq.getPassword().equals(""))
            {
                return new BasicResponse<>(BasicServerStatus.POST_USERS_EMPTY_PASSWORD);
            }

            // 입력한 비밀번호가 일치하지 않을시
            if(!postSignUpReq.getPassword().equals(postSignUpReq.getPassword2()))
            {
                return new BasicResponse<>(BasicServerStatus.POST_USERS_INCORRECT_PASSWORD);
            }

           // 닉네임 미입력시
            if(postSignUpReq.getPassword().equals(""))
            {
                return new BasicResponse<>(BasicServerStatus.POST_USERS_EMPTY_NICKNAME);
            }

            // 생년월일 미입력시
            if(postSignUpReq.getPassword().equals(""))
            {
                return new BasicResponse<>(BasicServerStatus.POST_USERS_EMPTY_BIRTHDAY);
            }

            // 중복된 이메일일시
            if(userService.checkUserExist(postSignUpReq.getUserId())){
                throw new BasicException(BasicServerStatus.POST_USERS_EXIST_EMAIL);
            }

            // 중복된 닉네임일시
            if(userService.checkNicknameExist(postSignUpReq.getNickname())){
                throw new BasicException(BasicServerStatus.POST_USERS_EXIST_NICKNAME);
            }
            PostSignUpRes postSignUpRes = userService.createSignUp(postSignUpReq);


            return new BasicResponse<>(postSignUpRes);
        }catch (BasicException exception){
            return new BasicResponse<>((exception.getStatus()));
        }
    }

}