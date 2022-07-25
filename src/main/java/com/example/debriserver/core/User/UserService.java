package com.example.debriserver.core.User;

import com.example.debriserver.basicModels.BasicException;
import com.example.debriserver.basicModels.BasicServerStatus;
import com.example.debriserver.core.User.Model.PostSignUpReq;
import com.example.debriserver.core.User.Model.PostSignUpRes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.example.debriserver.basicModels.BasicServerStatus.USERS_EMPTY_USER_ID;
import static com.example.debriserver.basicModels.BasicServerStatus.DB_ERROR;
@Service
public class UserService {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private final UserDao userDao;

    @Autowired
    private final UserProvider userProvider;

    public UserService(UserDao userDao, UserProvider userProvider) {
        this.userDao = userDao;
        this.userProvider = userProvider;
    }

    /**
     * 회원가입
     */
    public PostSignUpRes createSignUp(PostSignUpReq postSignUpReq) throws BasicException {

        try {

            PostSignUpRes postSignUpRes = userDao.createSignUp(postSignUpReq);

            return postSignUpRes;

        } catch (Exception exception) {
            System.out.println(exception.getMessage());
            throw new BasicException(BasicServerStatus.DB_ERROR);
        }

    }

    /**
     * 유저 아이디(이메일) 존재유무 확인
     */
    public boolean checkUserExist(String userId) throws BasicException{
        try{
            return userDao.checkUserExist(userId);
        }catch (Exception exception){
            throw new BasicException(BasicServerStatus.DB_ERROR);
        }
    }

    /**
     * 닉네임 존재유무 확인
     */
    public boolean checkNicknameExist(String nickname) throws BasicException{
        try{
            return userDao.checkNicknameExist(nickname);
        }catch (Exception exception){
            throw new BasicException(BasicServerStatus.DB_ERROR);
        }
    }

    /*public void modifyUser(int userIdx, int userIdx, PatchUserReq patchUserReq) throws BasicException {
        if (postProvider.checkUserExist(userIdx) == 0) {
            throw new BasicException(USERS_EMPTY_USER_ID);
        }

        if (postProvider.checkPostExist(postIdx) == 0) {
            throw new BasicException(POSTS_EMPTY_POST_ID);
        }

        try{

            int result = postDao.updatePost(postIdx, patchPostsReq.getPostContent());
            if (result == 0) {
                throw new BasicException(MODIFY_FAIL_POST);
            }
        }
        catch (Exception exception) {
            throw new BasicException(DB_ERROR);
        }
    }

    *//**
     * 유저 삭제 API
     * @param userId
     * @throws BasicException
     */
    public void deleteUser(String userId) throws BasicException {

        try{

            if (userProvider.checkUserExist(userId) == false) {
                throw new BasicException(USERS_EMPTY_USER_ID);
            }

            int result = userDao.deleteUser(userId);
            if (result == 0) {
                throw new BasicException(DB_ERROR);
            }
        }
        catch (Exception exception) {
            throw new BasicException(DB_ERROR);
        }
    }

}