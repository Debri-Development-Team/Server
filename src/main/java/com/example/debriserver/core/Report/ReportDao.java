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
}
