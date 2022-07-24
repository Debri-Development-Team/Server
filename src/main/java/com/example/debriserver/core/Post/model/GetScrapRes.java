package com.example.debriserver.core.Post.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class GetScrapRes {

    private int boardIdx;
    private int postIdx;
    private int userIdx;
    private String postName;
    private int likeNumber;
    private int timeAfterCreated;
    private int commentNumber;
}
