package com.example.debriserver.core.Post.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
//@NoArgsConstructor
public class GetPostRes {

    private int boardIdx;
    private int postIdx;
    private int authorIdx;
    private String postName;
    private String authorName;
    private String contents;
    private int LikeNumber;
    private int timeAfterCreated;
    private int commentNumber;
}
