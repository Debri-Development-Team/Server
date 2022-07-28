package com.example.debriserver.core.User;

import com.example.debriserver.basicModels.BasicException;
import com.example.debriserver.basicModels.BasicResponse;
import com.example.debriserver.basicModels.BasicServerStatus;
import com.example.debriserver.core.User.Model.PostSignUpReq;
import com.example.debriserver.core.User.Model.PostSignUpRes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import static com.example.debriserver.utility.ValidationRegex.isRegexEmail;


@RestController
@RequestMapping("/api/user")
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

    /**
     * 회원가입 API
     */
    @PostMapping("/signUp")
    public BasicResponse<PostSignUpRes> signUp(@RequestBody PostSignUpReq postSignUpReq) {
        try{
            // 이메일 (아이디) 미입력시
            if(postSignUpReq.getUserId().equals(""))
            {
                throw new BasicException(BasicServerStatus.POST_USERS_EMPTY_EMAIL);
            }

            // 이메일(아이디) 형식이 잘못된 경우
            if(!isRegexEmail(postSignUpReq.getUserId()))
            {
                throw new BasicException(BasicServerStatus.POST_USERS_INVALID_EMAIL);
            }

            // 비밀번호 미입력시
            if(postSignUpReq.getPassword().equals(""))
            {
                throw new BasicException(BasicServerStatus.POST_USERS_EMPTY_PASSWORD);
            }

            // 입력한 비밀번호가 일치하지 않을시
            if(!postSignUpReq.getPassword().equals(postSignUpReq.getPassword2()))
            {
                throw new BasicException(BasicServerStatus.POST_USERS_INCORRECT_PASSWORD);
            }

           // 닉네임 미입력시
            if(postSignUpReq.getNickname().equals(""))
            {
                throw new BasicException(BasicServerStatus.POST_USERS_EMPTY_NICKNAME);
            }

            // 생년월일 미입력시
            if(postSignUpReq.getBirthday().equals(""))
            {
                throw new BasicException(BasicServerStatus.POST_USERS_EMPTY_BIRTHDAY);
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

    /*@ResponseBody
    @PatchMapping("/{userIdx}")
    public BasicResponse<String> modifyUser(@PathVariable ("userIdx") int userIdx, @RequestBody PatchUserReq patchUserReq) {
        try{
            *//*String jwtToken = jwt.getJwt();

            if(jwt.isJwtExpired(jwtToken)) throw new BasicException(BasicServerStatus.EXPIRED_TOKEN);*//*

            *//*if (patchUserReq.getPostContent().length() > 5000) {
                return new BasicResponse<>(BasicServerStatus.POST_TOO_LONG_CONTENTS);
            }*//*

            userService.modifyUser(patchUserReq.getUserIdx(), userIdx, patchUserReq);
            String result = "게시물 정보 수정을 완료하였습니다.";
            return new BasicResponse<>(result);
        } catch(BasicException exception){
            return new BasicResponse<>((exception.getStatus()));
        }
    }*/

    // 회원 삭제
    @ResponseBody
    @PatchMapping("/{userId}/status")
    public BasicResponse<String> deleteUser(@PathVariable("userId") String userId){
        try {
            /*//
            //jwt에서 idx 추출.
            int userIdxByJwt = jwtService.getUserIdx();
            //userIdx와 접근한 유저가 같은지 확인
            if(userId != userIdxByJwt){
                return new BasicResponse<>(INVALID_USER_JWT);
            }
            //같다면 유저 삭제*/

            userService.deleteUser(userId);

            String result = "삭제되었습니다.";
            return new BasicResponse<>(result);
        } catch (BasicException exception) {
            return new BasicResponse<>((exception.getStatus()));
        }
    }


}