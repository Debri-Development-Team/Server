package com.example.debriserver.core.Report;

import com.example.debriserver.basicModels.BasicException;
import com.example.debriserver.core.Report.model.PostCommentReportReq;
import com.example.debriserver.core.Report.model.PostPostReportReq;
import com.example.debriserver.utility.jwtUtility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.example.debriserver.basicModels.BasicServerStatus.*;

@Service
public class ReportService {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private final ReportProvider reportProvider;

    @Autowired
    private final ReportDao reportDao;

    @Autowired
    private final jwtUtility jwt;

    public ReportService(ReportProvider reportProvider, ReportDao reportDao, jwtUtility jwt){
        this.reportProvider = reportProvider;
        this.reportDao = reportDao;
        this.jwt = jwt;
    }

    public void createPostReport(int userIdx, PostPostReportReq postPostReportReq) throws BasicException {

        if(reportProvider.checkUserExist(userIdx) == 0) {
            throw new BasicException(USERS_EMPTY_USER_ID);
        }

        if(reportProvider.checkUserExist(postPostReportReq.getPostIdx()) == 0) {
            throw new BasicException(POSTS_EMPTY_POST_ID);
        }

        try {
            int reportedUserIdx = reportProvider.findReportedPostUser(postPostReportReq.getPostIdx());
            reportDao.insertPostReport(userIdx, reportedUserIdx, postPostReportReq);
            reportDao.deleteReportedPost(postPostReportReq.getPostIdx());
            
        } catch (Exception exception) {
            throw new BasicException(DB_ERROR);
        }
    }

    public void createCommentReport(int userIdx, PostCommentReportReq postCommentReportReq) throws BasicException {

        if(reportProvider.checkUserExist(userIdx) == 0) {
            throw new BasicException(USERS_EMPTY_USER_ID);
        }

        if(reportProvider.checkCommentExist(postCommentReportReq.getCommentIdx()) == 0) {
            throw new BasicException(COMMENT_NOT_EXIST_ERROR);
        }

        try {
            int reportedUserIdx = reportProvider.findReportedCommentUser(postCommentReportReq.getCommentIdx());
            reportDao.insertCommentReport(userIdx, reportedUserIdx, postCommentReportReq);
            reportDao.deleteReportedComment(postCommentReportReq.getCommentIdx());

        } catch (Exception exception) {
            throw new BasicException(DB_ERROR);
        }
    }
}
