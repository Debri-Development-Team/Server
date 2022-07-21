package com.example.debriserver.core.User;

import com.example.debriserver.core.Auth.model.User;
import com.example.debriserver.core.User.Model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;

@Repository
public class UserDao {
    JdbcTemplate jdbcTemplate;

    @Autowired
    public void getDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public PostSignUpRes createSignUp(PostSignUpReq postSignUpReq) {
        //먼저 유저 이메일, 비밀번호, 닉네임,  생일, 프로필이미지URI를 저장
        String insertQuery = "INSERT\n" +
                "INTO User(userId, password, nickname, birthday, jwtRefreshToken)\n" +
                "VALUES (?, ?, ?, ?, 'Init');";

        Object[] insertUserParameters = new Object[]
                {
                        postSignUpReq.getUserId(),
                        postSignUpReq.getPassword(),
                        postSignUpReq.getNickname(),
                        postSignUpReq.getBirthday(),
                };
        this.jdbcTemplate.update(insertQuery, insertUserParameters);

        String getUserQuery = "select userIdx, userId, nickname from User where userId = ?";
        String getUserParams = postSignUpReq.getUserId();



        return this.jdbcTemplate.queryForObject(getUserQuery,
                (rs, rowNum) -> new PostSignUpRes(
                        rs.getInt("userIdx"),
                        rs.getString("userId"),
                        rs.getString("nickname")
                ), getUserParams);

    }

    public boolean checkUserExist(String userId) {
        String checkQuery = "SELECT COUNT(*) FROM User WHERE userId = ?;";

        int result = this.jdbcTemplate.queryForObject(checkQuery, int.class, userId);

        if(result == 0) return false;
        else return true;
    }

    public boolean checkNicknameExist(String nickname) {
        String checkQuery = "SELECT COUNT(*) FROM User WHERE nickname = ?;";

        int result = this.jdbcTemplate.queryForObject(checkQuery, int.class, nickname);

        if (result == 0) return false;
        else return true;
    }
}