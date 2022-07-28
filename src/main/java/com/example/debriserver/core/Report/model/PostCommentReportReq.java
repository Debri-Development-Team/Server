package com.example.debriserver.core.Report.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class PostCommentReportReq {

    private int commentIdx;
    private String reason;
}
