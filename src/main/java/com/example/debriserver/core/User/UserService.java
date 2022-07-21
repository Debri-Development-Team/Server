package com.example.debriserver.core.User;

import com.example.debriserver.basicModels.BasicException;
import com.example.debriserver.basicModels.BasicResponse;
import com.example.debriserver.basicModels.BasicServerStatus;
import com.example.debriserver.core.User.Model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.example.debriserver.utility.ValidationRegex.isRegexEmail;

@Service
public class UserService {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private final UserDao UserDao;

    public UserService(UserDao UserDao) {
        this.UserDao = UserDao;
    }

    public PostSignUpRes createSignUp(PostSignUpReq postSignUpReq) throws BasicException {

        try {

            PostSignUpRes postSignUpRes = UserDao.createSignUp(postSignUpReq);

            return postSignUpRes;

        } catch (Exception exception) {
            System.out.println(exception.getMessage());
            throw new BasicException(BasicServerStatus.DB_ERROR);
        }

    }

    public boolean checkUserExist(String userId) throws BasicException{
        try{
            return UserDao.checkUserExist(userId);
        }catch (Exception exception){
            throw new BasicException(BasicServerStatus.DB_ERROR);
        }
    }
    public boolean checkNicknameExist(String nickname) throws BasicException{
        try{
            return UserDao.checkNicknameExist(nickname);
        }catch (Exception exception){
            throw new BasicException(BasicServerStatus.DB_ERROR);
        }
    }
}