package com.example.debriserver.core.Board.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GetUnscrapBoardListRes {
    private int boardIdx;
    private String boardName;
    private String boardAdmin;
    private String createdAt;
    private String updatedAt;
}
