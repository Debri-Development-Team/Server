package com.example.debriserver.core.Post.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class GetScrapRes {

    private int postIdx;
    private int boardIdx;
    private String authorName;
    private String postName;
    private String likeStatus;
    private String scrapStatus;
    private int likeNumber;
    private int timeAfterCreated;
    private int commentNumber;
    private String boardName;
}
