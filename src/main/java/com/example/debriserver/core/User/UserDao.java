package com.example.debriserver.core.User;

import com.example.debriserver.core.User.Model.PostSignUpReq;
import com.example.debriserver.core.User.Model.PostSignUpRes;
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

    /**
     * 회원가입
     */
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

        String getUserQuery = "select userIdx, userId, nickname from User where userId = ? and status = 'ACTIVE' ";
        String getUserParams = postSignUpReq.getUserId();



        return this.jdbcTemplate.queryForObject(getUserQuery,
                (rs, rowNum) -> new PostSignUpRes(
                        rs.getInt("userIdx"),
                        rs.getString("userId"),
                        rs.getString("nickname")
                ), getUserParams);

    }

    /**
     * 유저 ID(Email) 존재유무
     * @param userId
     * @return
     */
    public boolean checkUserExist(String userId) {
        String checkQuery = "SELECT COUNT(*) FROM User WHERE userId = ? and status = 'ACTIVE';";

        int result = this.jdbcTemplate.queryForObject(checkQuery, int.class, userId);

        if(result == 0) return false;
        else return true;
    }


    /**
     * 닉네임 존재유무
     */
    public boolean checkNicknameExist(String nickname) {
        String checkQuery = "SELECT COUNT(*) FROM User WHERE nickname = ? and status = 'ACTIVE';";

        int result = this.jdbcTemplate.queryForObject(checkQuery, int.class, nickname);

        if (result == 0) return false;
        else return true;
    }
/*

   */
/* *//*
*/
/**
     *
     * @param postIdx
     * @param postContent
     * @return
     *//*
*/
/*

    public int updatUser(int userIdx, String postContent){
        String updateUserQuery = "UPDATE User SET postContent=? WHERE postIdx=?";
        Object []updatePostParams = new Object[]{postContent, postIdx};
        return this.jdbcTemplate.update(updatePostQuery, updatePostParams);
    }*//*


    */
/**
     * 유저 삭제 API
     * @param userId
     * @return
     */


    public int deleteUser(String userId){
        String deleteUserQuery = "UPDATE User SET status='INACTIVE' WHERE userId = ? and status = 'ACTIVE'";
        String deleteUserParams = userId;
        return this.jdbcTemplate.update(deleteUserQuery,
                deleteUserParams);
    }



}