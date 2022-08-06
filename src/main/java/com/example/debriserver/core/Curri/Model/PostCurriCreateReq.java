package com.example.debriserver.core.Curri.Model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PostCurriCreateReq {

    private String curriName;
    private String curriAuthor;
    private String visibleStatus;
    private String langTag;

    List<LectureForCurriCreateReq> lectureList;
}
