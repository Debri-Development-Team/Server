package com.example.debriserver.basicModels;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import static com.example.debriserver.basicModels.BasicServerStatus.SUCCESS;

/**
 * Basic Response Model class
 *
 * @author Rookie/이지호
 * @since 22.06.26
 * @modify Rookie/이지호
 * @updated 22.07.01
 * */

@Getter
@AllArgsConstructor
@JsonPropertyOrder({"isSuccess", "returnCode", "returnMsg", "result"})
public class BasicResponse<L> {
    @JsonProperty("isSuccess")
    private final Boolean isSuccess;
    private final int returnCode;
    private final String returnMsg;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private L result;

    //Request Success
    public BasicResponse(L result) {
        this.isSuccess = SUCCESS.isSuccess();
        this.returnCode = SUCCESS.getReturnCode();
        this.returnMsg = SUCCESS.getReturnMsg();
        this.result = result;
    }

    //Request Fail
    public BasicResponse(BasicServerStatus status) {
        this.isSuccess = status.isSuccess();
        this.returnCode = status.getReturnCode();
        this.returnMsg = status.getReturnMsg();
    }
}
