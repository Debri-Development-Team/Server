package com.example.debriserver.core.User;


import com.example.debriserver.basicModels.BasicException;
import com.example.debriserver.basicModels.BasicResponse;
import com.example.debriserver.core.User.Model.PostLoginReq;
import com.example.debriserver.core.User.Model.PostLoginRes;
import com.example.debriserver.core.User.Model.PostUserReq;
import com.example.debriserver.core.User.Model.PostUserRes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import static com.example.debriserver.basicModels.BasicServerStatus.POST_USERS_EMPTY_ID;
import static com.example.debriserver.basicModels.BasicServerStatus.POST_USERS_EMPTY_PASSWORD;

@RestController
@RequestMapping("/user")
public class UserController {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private final UserService userService;

    public UserController(UserService userService){
        this.userService = userService;
    }

    /**
     * 회원가입 API
     * [POST] /user/create
     * @return BaseResponse<PostUserRes>
     */
    // Body
    @ResponseBody
    @PostMapping("/create") // (POST) 127.0.0.1:9000/user/create
    public BasicResponse<PostUserRes> createUser(@RequestBody PostUserReq postUserReq) {

        if(postUserReq.getId() == null){
            return new BasicResponse<>(POST_USERS_EMPTY_ID);
        }

        try{
            PostUserRes postUserRes = userService.createUser(postUserReq);
            return new BasicResponse<>(postUserRes);
        } catch(BasicException exception){
            System.out.println(exception);
            return new BasicResponse<>((exception.getStatus()));
        }
    }

    @ResponseBody
    @PostMapping("/login")
    public BasicResponse<PostLoginRes> logIn(@RequestBody PostLoginReq postLoginReq) {
        try{

            if (postLoginReq.getId() == null) {
                return new BasicResponse<>(POST_USERS_EMPTY_ID);
            }

            if (postLoginReq.getPassword() == null) {
                return new BasicResponse<>(POST_USERS_EMPTY_PASSWORD);
            }

            PostLoginRes postLoginRes = userService.logIn(postLoginReq);

            return new BasicResponse<>(postLoginRes);
        } catch(BasicException exception){
            return new BasicResponse<>((exception.getStatus()));
        }
    }
}
