package com.example.debriserver.basicModels;

import lombok.Getter;

/**
 * Response Code Class
 *
 * @author Debri Server Team
 * @since 22.06.26
 * @implNote 수정이 너무 잦을거 같아서 따로 수정자, 수정일자는 적지 않겠습니다.
 * */

@Getter
public enum BasicServerStatus {
    /**
     * 200 : 성공
     * */
    SUCCESS(true, 200, "Request Success"),

    /**
     * 1000 : Database 오류
     * */
    DB_ERROR(false, 1000, "Database Error"),

    /**
     * 2000 : Server 오류
     * */
    SERVER_ERROR(false, 2000, "Server Error"),

    /**
     * 3000 : Request 오류
     * */
    REQUEST_ERROR(false, 3000, "Request Error"),

    //[Post]user
    POST_USERS_EMPTY_ID(false, 3010, "아이디를 입력해 주세요."),
    POST_USERS_EXISTS_ID(false,3011, "이미 존재하는 아이디입니다."),
    POST_USERS_EMPTY_PASSWORD(false, 3012, "패스워드를 입력해주세요."),
    FAILED_TO_LOGIN(false, 3020, "로그인에 실패했습니다."),

    /**
     * 4000 : Response 오류
     * */
    RESPONSE_ERROR(false, 4000,"Response Error");

    private final boolean isSuccess;
    private final int returnCode;
    private final String returnMsg;

    BasicServerStatus(boolean isSuccess, int returnCode, String returnMsg){
        this.isSuccess = isSuccess;
        this.returnCode = returnCode;
        this.returnMsg = returnMsg;
    }
}
