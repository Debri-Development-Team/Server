package com.example.debriserver.core.Curri.Model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PostCurriCreateRes {
    private int curriIdx;
    private String curriName;
    private String curriAuthor;
    private int createdAt;
}
