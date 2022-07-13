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
    COMMENT_TOO_LONG_ERROR(false, 3050, "내용의 길이가 너무 깁니다(최대 5000자)"),
    COMMENT_NOT_EXIST_ERROR(false, 3051, "댓글이나 대댓글이 존재하지 않습니다."),
    COMMENT_POST_DELETED_ERROR(false, 3052, "게시물이 삭제되어 댓글 작성이 불가합니다."),
    ROOT_COMMENT_DELETED_ERROR(false, 3053, "댓글이 삭제되어 대댓글 작성이 불가합니다."),

    /**
     * 4000 : Response 오류
     * */
    RESPONSE_ERROR(false, 4000,"Response Error"),

    /**
     * 5000 : Jwt 오류
     * */
    JWT_NOT_EXIST(false, 5000, "AccessToken 이 없습니다."),
    EXPIRED_TOKEN(false, 5001, "AccessToken Expired"),
    INVALID_JWT(false, 5002, "Invalid Jwt Token"),
    INVALID_SIGNATURE(false, 5003, "Invalid Jwt Signature"),
    UNSUPPORTED_JWT(false, 5004, "Unsupported Jwt"),
    EMPTY_JWT_CLAIMS_STRING(false, 5005, "Jwt Claims String Empty");

    private final boolean isSuccess;//
    private final int returnCode;
    private final String returnMsg;

    BasicServerStatus(boolean isSuccess, int returnCode, String returnMsg){
        this.isSuccess = isSuccess;
        this.returnCode = returnCode;
        this.returnMsg = returnMsg;
    }
}
