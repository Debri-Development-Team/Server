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
    private int userIdx;
    private String postContent;
    private String postName;
    private String createdAt;
    private String updatedAt;
}
