package com.example.debriserver.core.Auth;

import com.example.debriserver.basicModels.BasicException;
import com.example.debriserver.basicModels.BasicServerStatus;
import com.example.debriserver.core.Auth.model.PostAuthEmailRes;
import com.example.debriserver.core.Auth.model.PostLoginReq;
import com.example.debriserver.core.Auth.model.PostLoginRes;
import com.example.debriserver.core.Auth.model.User;
import com.example.debriserver.utility.jwtUtility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired
    private JavaMailSender javaMailSender;
    private final AuthDao authDao;
    private final AuthProvider authProvider;
    private final jwtUtility jwtUtility;

    final Logger logger = LoggerFactory.getLogger(this.getClass());


    public AuthService(AuthDao authDao, AuthProvider authProvider, jwtUtility jwtUtility)
    {
        this.authDao = authDao;
        this.authProvider = authProvider;
        this.jwtUtility = jwtUtility;
    }

    public PostLoginRes login(PostLoginReq postLoginReq) throws BasicException
    {
        User user = authDao.getUser(postLoginReq);

        if(user.getPassword().equals(postLoginReq.getPwd()))
        {
            int userIdx = user.getUserIdx();
            String userName = user.getNickname();
            String jwt = jwtUtility.createToken(userIdx);
            String refreshToken = jwtUtility.createRefreshToken();

            authDao.insertRefresh(refreshToken, postLoginReq.getEmail());

            return new PostLoginRes(userIdx, userName, user.getUserId(), user.getBirthday(), jwt, refreshToken, authDao.checkFirstLogin(postLoginReq.getEmail()));
        }
        else
        {
            throw new BasicException(BasicServerStatus.FAILED_TO_LOGIN);
        }

    }

    public PostAuthEmailRes authEmail(String email) throws BasicException
    {
        if(!authDao.checkEmailExist(email)){
            throw new BasicException(BasicServerStatus.EMAIL_EXIST_ERROR);
        }

        SimpleMailMessage msg = new SimpleMailMessage();

        // 유효시간 => 300000(msc) = 5(min)
        int timeout = 300000;

        // 인증번호 생성
        int authNumber = (int)(Math.random() * (9999 - 1000 + 1)) + 1000;

        // 메일 보낼 대상 이메일 주소 세팅
        msg.setTo(email);

        // 메일 제목 세팅
        msg.setSubject("데브리(Debri) 인증번호");

        // 메일 본문 내용 세팅 ( 인증번호 )
        msg.setText("데브리(Debri) 인증번호는 [" + authNumber + "] 입니다.");

        System.out.println("email : " + email);
        System.out.println("authNumber : " + authNumber);
        System.out.println("timeout : " + timeout);

        // 메일 보내기
        javaMailSender.send(msg);

        return new PostAuthEmailRes(authNumber, timeout);
    }

}
