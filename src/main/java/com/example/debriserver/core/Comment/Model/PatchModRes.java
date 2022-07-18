package com.example.debriserver.core.Comment.Model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PatchModRes {
    private boolean modSuccess;
    private int commentIdx;
    private String content;
    private int commentLevel;
    private int commentOrder;
    private int commentGroup;
}
