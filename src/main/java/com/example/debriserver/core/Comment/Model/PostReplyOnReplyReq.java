package com.example.debriserver.core.Comment.Model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class PostReplyOnReplyReq {
    private int userIdx;
    private int postIdx;
    private int rootCommentIdx;
    private String content;
    private String authorName;
}
