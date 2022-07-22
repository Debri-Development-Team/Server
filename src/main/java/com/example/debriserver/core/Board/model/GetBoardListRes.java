package com.example.debriserver.core.Board.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GetBoardListRes {
    private int boardIdx;
    private String boardName;
    private String boardAdmin;
}
