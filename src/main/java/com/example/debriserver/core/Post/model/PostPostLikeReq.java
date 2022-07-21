package com.example.debriserver.core.Post.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class PostPostLikeReq {

    private int postIdx;
    private int userIdx;
    private String likeStatus;

}
