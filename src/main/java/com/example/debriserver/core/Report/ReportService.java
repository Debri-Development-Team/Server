package com.example.debriserver.core.Report;

import com.example.debriserver.utility.jwtUtility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
}
