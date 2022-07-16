package com.example.debriserver.core.Post.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class PostPostsReq {

    private int boardIdx;
    private int userIdx;
    private String postContent;
    private String postName;
    private List<PostImgUrlReq> postImgUrls;

}
