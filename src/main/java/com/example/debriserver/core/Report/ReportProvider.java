package com.example.debriserver.core.Report;

import com.example.debriserver.utility.jwtUtility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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


}
