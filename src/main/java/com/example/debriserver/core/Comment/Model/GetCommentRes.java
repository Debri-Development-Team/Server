package com.example.debriserver.core.Comment.Model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class GetCommentRes {
    private int commentIdx;
    private int userIdx;
    private int postIdx;
    private int commentLevel;
    private int commentOrder;
    private int commentGroup;
    private String commentContent;
    private String authorName;
}