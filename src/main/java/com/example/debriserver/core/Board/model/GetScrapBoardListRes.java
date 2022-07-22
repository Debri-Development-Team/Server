package com.example.debriserver.core.Board.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class GetScrapBoardListRes {

    private int boardIdx;
    private String boardName;
    private String boardAdmin;
    private String createdAt;
    private String updatedAt;
    private String status;

}
