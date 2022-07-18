package com.example.debriserver.core.Comment.Model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PostReplyOnPostRes {
    private String commentContents;
    private int userIdx;
    private int commentIdx;
    private int commentLevel;
    private int commentGroup;
    private int commentOrder;
    private String authorName;
}
