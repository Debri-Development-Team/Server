package com.example.debriserver.core.Report;

import com.example.debriserver.basicModels.BasicException;
import com.example.debriserver.basicModels.BasicResponse;
import com.example.debriserver.basicModels.BasicServerStatus;
import com.example.debriserver.core.Report.model.PostCommentReportReq;
import com.example.debriserver.core.Report.model.PostPostReportReq;
import com.example.debriserver.core.Report.model.PostReportUserReq;
import com.example.debriserver.utility.jwtUtility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/report")
public class ReportController {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private final ReportProvider reportProvider;

    @Autowired
    private final ReportService reportService;

    @Autowired
    private final jwtUtility jwt;

    public ReportController(ReportProvider reportProvider, ReportService reportService, jwtUtility jwt){
        this.reportProvider = reportProvider;
        this.reportService = reportService;
        this.jwt = jwt;
    }

    @ResponseBody
    @PostMapping("/user/{postIdx}")
    public BasicResponse<String> reportUser(@PathVariable("postIdx") int postIdx, @RequestBody PostReportUserReq postReportUserReq)
    {
        try{
            String jwtToken = jwt.getJwt();
            if (jwt.isJwtExpired(jwtToken)) throw new BasicException(BasicServerStatus.EXPIRED_TOKEN);

            int reportUserIdx = jwt.getUserIdx();
            String reason = postReportUserReq.getReason();
            String result = "사용자 신고가 완료되었습니다.";

            reportService.reportUser(reportUserIdx, postIdx, reason);

            return new BasicResponse<>(result);

        } catch (BasicException exception) {
            return new BasicResponse<>((exception.getStatus()));
        }

    }

    @PostMapping("/postReport")
    public BasicResponse<String> createPostReport(@RequestBody PostPostReportReq postPostReportReq) {

        try {
            String jwtToken = jwt.getJwt();

            if(jwt.isJwtExpired(jwtToken)) throw new BasicException(BasicServerStatus.EXPIRED_TOKEN);

            int userIdx = jwt.getUserIdx(jwtToken);

            reportService.createPostReport(userIdx, postPostReportReq);

            String result = "게시물 신고가 접수되었습니다.";

            return new BasicResponse<>(result);
        } catch(BasicException exception){
            return new BasicResponse<>((exception.getStatus()));
        }

    }

    @PostMapping("/commentReport")
    public BasicResponse<String> createCommentReport(@RequestBody PostCommentReportReq postCommentReportReq) {

        try {
            String jwtToken = jwt.getJwt();

            if(jwt.isJwtExpired(jwtToken)) throw new BasicException(BasicServerStatus.EXPIRED_TOKEN);

            int userIdx = jwt.getUserIdx(jwtToken);

            reportService.createCommentReport(userIdx, postCommentReportReq);

            String result = "댓글 신고가 접수되었습니다.";

            return new BasicResponse<>(result);
        } catch(BasicException exception){
            return new BasicResponse<>((exception.getStatus()));
        }

    }
}
