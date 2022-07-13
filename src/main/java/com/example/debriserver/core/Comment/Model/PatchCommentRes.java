package com.example.debriserver.core.Comment.Model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PatchCommentRes {
    private boolean deleteSuccess;
    private int commentIdx;
    private int postIdx;
    private int commentLevel;
    private int commentOrder;
    private int commentGroup;

    public boolean getDeleteSuccess(){return deleteSuccess;}
}
