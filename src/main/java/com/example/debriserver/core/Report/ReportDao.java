package com.example.debriserver.core.Report;

import com.example.debriserver.basicModels.BasicException;
import com.example.debriserver.basicModels.BasicServerStatus;
import com.example.debriserver.core.Report.model.PostCommentReportReq;
import com.example.debriserver.core.Report.model.PostPostReportReq;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;

@Repository
public class ReportDao {
    JdbcTemplate jdbcTemplate;

    @Autowired
    public void getDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    /**
     * 신고 당한 게시물 작성자 찾기
     */
    public int findReportedPostUser(int postIdx) {
        String findReportedUserQuery = "SELECT userIdx FROM Post WHERE postIdx = ?";
        int findReportedUserParams = postIdx;
        return this.jdbcTemplate.queryForObject(findReportedUserQuery,
                int.class,
                findReportedUserParams);
    }

    /**
     * ReportedPost 테이블에 신고된 게시물 데이터 추가
     */
    public void insertPostReport(int userIdx, int reportedUserIdx, PostPostReportReq postPostReportReq) {

        String insertPostReportQuery = "INSERT INTO ReportedPost(reportUserIdx, reportedUserIdx, postIdx, reason) VALUES(?,?,?,?)";
        Object[] insertPostReportParams = new Object[]{
                userIdx,
                reportedUserIdx,
                postPostReportReq.getPostIdx(),
                postPostReportReq.getReason()
        };
        this.jdbcTemplate.update(insertPostReportQuery, insertPostReportParams);
    }

    /**
     * 신고된 게시물 삭제
     */
    public void deleteReportedPost(int postIdx) {
        String deleteReportedPostQuery = "UPDATE Post SET status = 'DELETE' WHERE postIdx = ?";
        int deleteReportedPostParams = postIdx;

        this.jdbcTemplate.update(deleteReportedPostQuery,
                deleteReportedPostParams);
    }

    /**
     * 신고 당한 댓글 작성자 찾기
     */
    public int findReportedCommentUser(int commentIdx) {
        String findReportedCommentUserQuery = "SELECT userIdx FROM Comment WHERE commentIdx = ?";
        int findReportedCommentUserParams = commentIdx;
        return this.jdbcTemplate.queryForObject(findReportedCommentUserQuery,
                int.class,
                findReportedCommentUserParams);
    }

    /**
     * ReportedComment 테이블에 신고된 댓글 데이터 추가
     */
    public void insertCommentReport(int userIdx, int reportedUserIdx, PostCommentReportReq postCommentReportReq) {
        String insertCommentReportQuery = "INSERT INTO ReportedComment(reportUserIdx, reportedUserIdx, commentIdx, reason) VALUES(?,?,?,?)";
        Object[] insertCommentReportParams = new Object[]{
                userIdx,
                reportedUserIdx,
                postCommentReportReq.getCommentIdx(),
                postCommentReportReq.getReason()
        };
        this.jdbcTemplate.update(insertCommentReportQuery, insertCommentReportParams);
    }

    /**
     * 신고된 댓글 삭제
     */
    public void deleteReportedComment(int commentIdx) {
        String deleteReportedCommentQuery = "UPDATE Comment SET status = 'DELETE' WHERE commentIdx = ?";
        int deleteReportedCommentParams = commentIdx;

        this.jdbcTemplate.update(deleteReportedCommentQuery,
                deleteReportedCommentParams);
    }

    public int checkUserExist(int userIdx){
        String checkUserExistQuery = "SELECT exists(SELECT userIdx FROM User WHERE userIdx = ? AND status = 'ACTIVE')";
        int checkUserExistParams = userIdx;
        return this.jdbcTemplate.queryForObject(checkUserExistQuery,
                int.class,
                checkUserExistParams);

    }

    public int checkPostExist(int postIdx){
        String checkPostExistQuery = "SELECT exists(SELECT postIdx FROM Post WHERE postIdx = ? AND status = 'ACTIVE')";
        int checkPostExistParams = postIdx;
        return this.jdbcTemplate.queryForObject(checkPostExistQuery,
                int.class,
                checkPostExistParams);

    }

    public int checkCommentExist(int commentIdx) {
        String checkCommentExistQuery = "SELECT exists(SELECT commentIdx FROM Comment WHERE commentIdx = ? AND status = 'ACTIVE')";
        int checkCommentExistParams = commentIdx;
        return this.jdbcTemplate.queryForObject(checkCommentExistQuery,
                int.class,
                checkCommentExistParams);
    }
}
