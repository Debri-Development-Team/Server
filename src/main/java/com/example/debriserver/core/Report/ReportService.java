package com.example.debriserver.core.Report;

import com.example.debriserver.basicModels.BasicException;
import com.example.debriserver.utility.jwtUtility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.example.debriserver.basicModels.BasicServerStatus.DB_ERROR;

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


    /**
     * 유저 신고
     * 신고 데이터가 ReportedUser 테이블에 추가됨
     * 신고된 유저의 데이터는 PostDao와 CommentDao에서 걸러질 예정
     * */
    public void reportUser(int reportUserIdx, int postIdx, String reason) throws BasicException
    {
        try{
            int result = reportDao.reportUser(reportUserIdx, postIdx, reason);
        } catch (Exception exception) {
            throw new BasicException(DB_ERROR);
        }
    }
}
