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
        String getUserQuery = "select userIdx, userId, password, nickname, birthday from User where userId = ?";
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
        String insertRefreshQuery = "UPDATE User SET jwtRefreshToken = ? WHERE userId =?;";
        Object[] insertRefreshParameters = new Object[] {
                Refresh,
                userId
        };

        this.jdbcTemplate.update(insertRefreshQuery, insertRefreshParameters);
    }
}
