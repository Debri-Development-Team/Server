package com.example.debriserver.core.User;

import com.example.debriserver.basicModels.BasicException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.example.debriserver.basicModels.BasicServerStatus.DB_ERROR;

//Provider : Read의 비즈니스 로직 처리
@Service
public class UserProvider {


    private final UserDao userDao;

    /*private final JwtService jwtService;*/


    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    public UserProvider(UserDao userDao/*, JwtService jwtService*/) {
        this.userDao = userDao;
        /*this.jwtService = jwtService;*/
    }


    /*// 회원 피드 조회
    public GetUserFeedRes retrieveUserFeed(int userIdx, int userIdxByJwt) throws BasicException {

        if (checkUserExist(userIdx) == 0) {
            throw new BasicException(USERS_EMPTY_USER_ID);
        }

        Boolean isMyFeed = true;
        try {
            if (userIdxByJwt != userIdx) {
                isMyFeed = false;
            }
            GetUserInfoRes getUserInfo = userDao.selectUserInfo(userIdx);
            List<GetUserPostsRes> getUserPosts = userDao.selectUserPosts(userIdx);
            GetUserFeedRes getUserFeed = new GetUserFeedRes(isMyFeed, getUserInfo, getUserPosts);
            return getUserFeed;
        } catch (Exception exception) {
            throw new BasicException(DB_ERROR);
        }
    }

    // 이메일 확인
    public int checkEmailExist(String email) throws BasicException {
        try {
            return userDao.checkEmailExist(email);
        } catch (Exception exception) {
            throw new BasicException(DB_ERROR);
        }
    }
*/
    // 유저 확인
    public boolean checkUserExist(String userId) throws BasicException {
        try {
            return userDao.checkUserExist(userId);
        } catch (Exception exception) {
            throw new BasicException(DB_ERROR);
        }
    }

    // 닉네임 확인
    public boolean checkNickNameExist(String nickname) throws BasicException {
        try {
            return userDao.checkNicknameExist(nickname);
        } catch (Exception exception) {
            throw new BasicException(DB_ERROR);
        }
    }
}
