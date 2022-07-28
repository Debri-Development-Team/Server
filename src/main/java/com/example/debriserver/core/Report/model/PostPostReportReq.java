package com.example.debriserver.core.Report.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class PostPostReportReq {

    private int postIdx;
    private String reason;

}
