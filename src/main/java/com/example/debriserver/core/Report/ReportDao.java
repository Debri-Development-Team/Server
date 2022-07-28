package com.example.debriserver.core.Report;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;

@Repository
public class ReportDao {
    JdbcTemplate jdbcTemplate;

    @Autowired
    public void getDataSource(DataSource dataSource){
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public int reportUser(int reportUserIdx, int postIdx, String reason)
    {
        String reportUserQuery = "INSERT INTO ReportedUser(reportUserIdx, reportedUserIdx, reason) VALUES (?, (select userIdx from Post where postIdx = ?), ?);";
        Object[] reportUserParams = new Object[]{
                reportUserIdx,
                postIdx,
                reason
        };
        return this.jdbcTemplate.update(reportUserQuery, reportUserParams);
    }

    public int checkUserExist(int userIdx)
    {
        String checkUserExistQuery = "select exists(select userIdx from User where userIdx = ? and status = 'ACTIVE')";
        int checkUserExistParams = userIdx;
        return this.jdbcTemplate.queryForObject(checkUserExistQuery,
                int.class,
                checkUserExistParams);
    }

    public int checkPostExist(int postIdx)
    {
        String checkPostExistQuery = "select exists(select postIdx from Post where postIdx = ? and status = 'ACTIVE')";
        int checkPostExistParams = postIdx;
        return this.jdbcTemplate.queryForObject(checkPostExistQuery,
                int.class,
                checkPostExistParams);
    }
}
