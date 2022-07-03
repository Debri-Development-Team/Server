package com.example.debriserver.core.user;

import com.example.debriserver.basicModels.BasicException;
import com.example.debriserver.basicModels.BasicServerStatus;
import com.example.debriserver.core.user.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.example.debriserver.basicModels.BasicServerStatus.*;

@Service
public class UserService {

    private final UserDao userDao;
    private final UserProvider userProvider;

    @Autowired
    public UserService(UserDao userDao, UserProvider userProvider) {
        this.userDao = userDao;
        this.userProvider = userProvider;
    }


    public PostUserRes createUser(PostUserReq postUserReq) throws BasicException {
        // 아이디 중복 확인
        if (userProvider.checkId(postUserReq.getId()) == 1) {
            throw new BasicException(POST_USERS_EXISTS_ID);
        }

        try {
            int userIdx = userDao.createUser(postUserReq);
            return new PostUserRes(userIdx);
        } catch (Exception exception) {
            System.out.println(exception);
            throw new BasicException(DB_ERROR);
        }
    }

    public PostLoginRes logIn(PostLoginReq postLoginReq) throws BasicException{
        User user = userDao.getPassword(postLoginReq);

        if (user.getPassword().equals(postLoginReq.getPassword())) {
            int userIdx = user.getUserIdx();
            return new PostLoginRes(userIdx);
        }
        else {
            throw new BasicException(FAILED_TO_LOGIN);
        }
    }

}
