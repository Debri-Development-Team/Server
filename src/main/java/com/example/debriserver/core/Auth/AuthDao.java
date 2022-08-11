package com.example.debriserver.core.Auth;

import com.example.debriserver.core.Auth.model.PostLoginReq;
import com.example.debriserver.core.Auth.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;

@Repository
public class AuthDao {

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public User getUser(PostLoginReq postLoginReq)
    {
        String getUserQuery = "select userIdx, userId, password, nickname, birthday from User where userId = ? and status = 'ACTIVE'";
        String getUserParams = postLoginReq.getEmail();


        return this.jdbcTemplate.queryForObject(getUserQuery,
                (rs, rowNum) -> new User(
                        rs.getInt("userIdx"),
                        rs.getString("userId"),
                        rs.getString("password"),
                        rs.getString("nickname"),
                        rs.getString("birthday")
                ), getUserParams);


    }

    public void insertRefresh(String Refresh,String userId){
        String insertRefreshQuery = "UPDATE User SET jwtRefreshToken = ? WHERE userId =? and status = 'ACTIVE';";
        Object[] insertRefreshParameters = new Object[] {
                Refresh,
                userId
        };

        this.jdbcTemplate.update(insertRefreshQuery, insertRefreshParameters);
    }

    public boolean checkFirstLogin(String userId){

        String checkQuery = "SELECT COUNT(*) FROM User WHERE isFirst = 'TRUE' and userId = ?;";
        String updateQuery = "UPDATE User SET isFirst = 'FALSE' WHERE userId = ?;";

        boolean result = this.jdbcTemplate.queryForObject(checkQuery, int.class, userId) > 0;

        if(result) this.jdbcTemplate.update(updateQuery, userId);

        return result;
    }
}
