package com.example.debriserver.core.Report.Model;

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
