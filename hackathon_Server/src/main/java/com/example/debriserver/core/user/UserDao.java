package com.example.debriserver.core.user;

import com.example.debriserver.core.user.model.PostLoginReq;
import com.example.debriserver.core.user.model.PostUserReq;
import com.example.debriserver.core.user.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;

@Repository
public class UserDao {
    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource) {this.jdbcTemplate = new JdbcTemplate(dataSource);}

    public int createUser(PostUserReq postUserReq){
        String createUserQuery = "INSERT INTO User (id, password, nickname, birthday) VALUE (?,?,?,?)";
        Object[] createUserParams = new Object[]{postUserReq.getId(), postUserReq.getPassword(), postUserReq.getNickname(), postUserReq.getBirthday()};
        this.jdbcTemplate.update(createUserQuery, createUserParams);

        String lastInsertIdQuery = "select last_insert_id()";
        return this.jdbcTemplate.queryForObject(lastInsertIdQuery,int.class);
    }

    public int checkId(String id){
        String checkIdQuery = "select exists(select id from User where id = ?)";
        String checkIdParams = id;
        return this.jdbcTemplate.queryForObject(checkIdQuery,
                int.class,
                checkIdParams);

    }

    public User getPassword(PostLoginReq postLoginReq) {
        String getPasswordQuery = "select userIdx, id, password, nickname, birthday from User where id = ?";
        String getPasswordParams = postLoginReq.getId();

        return this.jdbcTemplate.queryForObject(getPasswordQuery,
                (rs, rowNum)-> new User(
                        rs.getInt("userIdx"),
                        rs.getString("id"),
                        rs.getString("password"),
                        rs.getString("nickname"),
                        rs.getString("birthday")
                ),
                getPasswordParams
        );
    }
}
