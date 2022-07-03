package com.example.debriserver.basicModels;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * Basic Exception Model class
 *
 * @author Rookie/이지호
 * @since 22.06.26
 * @modify 수정자 이름 적어주세요
 * @updated 수정일자 적어주세요
 * */

@Getter
@Setter
@AllArgsConstructor
public class BasicException extends Exception{
    private BasicServerStatus status;
}
