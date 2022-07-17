package com.example.debriserver.core.Comment.Model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PostReplyOnReplyRes {
    private String commentContents;//
    private int userIdx;//
    private int postIdx;//
    private int commentIdx;
    private int commentGroup;
    private int commentOrder;
    private int level;
    private String userName;//
}
