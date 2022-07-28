package com.example.debriserver.core.Report;

import com.example.debriserver.basicModels.BasicException;
import com.example.debriserver.basicModels.BasicServerStatus;
import com.example.debriserver.utility.jwtUtility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.example.debriserver.basicModels.BasicServerStatus.DB_ERROR;

@Service
public class ReportProvider {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private final ReportDao reportDao;

    @Autowired
    private final jwtUtility jwt;

    public ReportProvider(ReportDao reportDao, jwtUtility jwt){
        this.reportDao = reportDao;
        this.jwt = jwt;
    }

    public int findReportedPostUser(int postIdx) throws BasicException {
          try {
              return reportDao.findReportedPostUser(postIdx);
          } catch (Exception exception) {
            throw new BasicException(DB_ERROR);
          }
    }
    
    public int checkUserExist(int userIdx) throws BasicException
    {
        try{
            return reportDao.checkUserExist(userIdx);
        } catch (Exception exception) {
            throw new BasicException(DB_ERROR);
        }
    }

    public int findReportedCommentUser(int commentIdx) throws BasicException {
        try {
            return reportDao.findReportedCommentUser(commentIdx);
        } catch (Exception exception) {
            throw new BasicException(DB_ERROR);
        }
    }

    public int checkUserExist(int userIdx) throws BasicException {
        try{
            return reportDao.checkUserExist(userIdx);
        } catch (Exception exception) {
            throw new BasicException(DB_ERROR);
        }
    }

    public int checkPostExist(int postIdx) throws BasicException{
        try{
            return reportDao.checkPostExist(postIdx);
        } catch (Exception exception) {
            throw new BasicException(DB_ERROR);
        }
    }

    public int checkCommentExist(int commentIdx) throws BasicException{
        try{
            return reportDao.checkCommentExist(commentIdx);
        } catch (Exception exception) {
            throw new BasicException(DB_ERROR);
        }
    }
}
