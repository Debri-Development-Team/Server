package com.example.debriserver.core.Report;

import com.example.debriserver.utility.jwtUtility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

@RestController
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
        this. jwt = jwt;
    }



}
