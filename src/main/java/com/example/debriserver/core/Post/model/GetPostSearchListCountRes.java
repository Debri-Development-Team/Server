package com.example.debriserver.core.Post.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class GetPostSearchListCountRes {
    private List<GetPostSearchListRes> postList;
    private int postCount;
}
