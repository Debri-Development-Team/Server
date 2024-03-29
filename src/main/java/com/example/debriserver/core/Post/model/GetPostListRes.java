package com.example.debriserver.core.Post.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GetPostListRes {

    private int boardIdx;
    private int postIdx;
    private String authorName;
    private String postName;
    private int likeNumber;
    private String likeStatus;
    private String scrapStatus;
    private int timeAfterCreated;
    private int commentNumber;
    private String boardName;
}
