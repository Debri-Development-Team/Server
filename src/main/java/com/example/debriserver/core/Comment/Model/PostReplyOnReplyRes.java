package com.example.debriserver.core.Comment.Model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PostReplyOnReplyRes {
    private String commentContent;//
    private int authorIdx;//
    private int postIdx;//
    private int commentIdx;
    private int commentLevel;
    private int commentGroup;
    private int commentOrder;
    private String authorName;//
}
