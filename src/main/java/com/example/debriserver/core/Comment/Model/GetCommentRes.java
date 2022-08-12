package com.example.debriserver.core.Comment.Model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class GetCommentRes {
    private int commentIdx;
    private int authorIdx;
    private int postIdx;
    private int commentLevel;
    private int commentOrder;
    private int commentGroup;
    private int timeAfterCreated;
    private String commentContent;
    private String authorName;
    private boolean likeStatus;
    private int likeCount;
}
