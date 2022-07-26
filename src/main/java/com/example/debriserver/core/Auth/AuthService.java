package com.example.debriserver.core.Auth;

import com.example.debriserver.basicModels.BasicException;
import com.example.debriserver.basicModels.BasicServerStatus;
import com.example.debriserver.core.Auth.model.PostLoginReq;
import com.example.debriserver.core.Auth.model.PostLoginRes;
import com.example.debriserver.core.Auth.model.User;
import com.example.debriserver.utility.jwtUtility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final AuthDao authDao;
    private final AuthProvider authProvider;
    private final jwtUtility jwtUtility;

    public AuthService(AuthDao authDao, AuthProvider authProvider, jwtUtility jwtUtility)
    {
        this.authDao = authDao;
        this.authProvider = authProvider;
        this.jwtUtility = jwtUtility;
    }

    public PostLoginRes login(PostLoginReq postLoginReq) throws BasicException
    {
        User user = authDao.getUser(postLoginReq);
        String encryptPwd;

//        // 입력 받은 password를 암호화시키는 과정
//        try{
//            encryptPwd = new SHA256().encrypt(postLoginReq.getPwd());
//        }catch (Exception exception)
//        {
//            throw new BasicException(BasicServerStatus.PASSWORD_ENCRYPTION_ERROR);
//        }

//        // 유저로부터 입력 받은 이메일로 DB에서 password를 찾아 유저로부터 입력 받은 password와 비교
//        if(user.getPassword().equals(encryptPwd))
//        {
//            int userIdx = user.getUserIdx();
//            String jwt = jwtUtility.createToken(userIdx);
//            return new PostLoginRes(userIdx, jwt);
//        }
//        else
//        {
//            throw new BasicException(BasicServerStatus.FAILED_TO_LOGIN);
//        }

        if(user.getPassword().equals(postLoginReq.getPwd()))
        {
            int userIdx = user.getUserIdx();
            String userName = user.getNickname();
            String jwt = jwtUtility.createToken(userIdx);
            String refreshToken = jwtUtility.createRefreshToken();

            authDao.insertRefresh(refreshToken, postLoginReq.getEmail());

            return new PostLoginRes(userIdx, userName, jwt, refreshToken);
        }
        else
        {
            throw new BasicException(BasicServerStatus.FAILED_TO_LOGIN);
        }

    }



}
