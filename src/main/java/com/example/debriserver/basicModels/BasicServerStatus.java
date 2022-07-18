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
    PASSWORD_ENCRYPTION_ERROR(false, 2010, "비밀번호 암호화에 실패하였습니다."),
    PASSWORD_DECRYPTION_ERROR(false, 2011, "비밀번호 복호화에 실패하였습니다."),
    USERS_EMPTY_USER_ID(false, 2012, "유저 아이디를 확인해주세요."),
    /**
     * 3000 : Request 오류
     * */
    REQUEST_ERROR(false, 3000, "Request Error"),
    POST_TOO_LONG_CONTENTS(false, 3030, "게시물 내용의 길이가 너무 깁니다.(최대 5000자)"),
    POSTS_EMPTY_POST_ID(false, 3031, "게시물의 아이디를 확인해주세요."),
    POST_EMPTY_IMG_URL(false, 3032, "게시물의 이미지를 넣어주세요."),
    MODIFY_FAIL_POST(false, 3033, "게시물 수정 실패"),

    //COMMENT
    COMMENT_TOO_LONG_ERROR(false, 3050, "내용의 길이가 너무 깁니다(최대 5000자)"),
    COMMENT_NOT_EXIST_ERROR(false, 3051, "댓글이나 대댓글이 존재하지 않습니다."),
    COMMENT_POST_DELETED_ERROR(false, 3052, "게시물이 삭제되어 댓글 작성이 불가합니다."),
    ROOT_COMMENT_DELETED_ERROR(false, 3053, "댓글이 삭제되어 대댓글 작성이 불가합니다."),

    // [POST]
    POST_USERS_EMPTY_EMAIL(false, 3020, "이메일을 입력해주세요."),
    POST_USERS_EMPTY_PASSWORD(false, 3021, "비밀번호를 입력해주세요."),
    POST_USERS_INVALID_EMAIL(false, 3022, "이메일 형식을 확인해주세요."),

    /**
     * 4000 : Response 오류
     * */
    RESPONSE_ERROR(false, 4000,"Response Error"),

    // [POST]
    FAILED_TO_LOGIN(false, 4010, "없는 아이디거나 비밀번호가 틀렸습니다."),


    /**
     * 5000 : Jwt 오류
     * */
    JWT_NOT_EXIST(false, 5000, "AccessToken 이 없습니다."),
    EXPIRED_TOKEN(false, 5001, "AccessToken Expired(갱신 필요)"),
    INVALID_JWT(false, 5002, "Invalid Jwt Token"),
    INVALID_SIGNATURE(false, 5003, "Invalid Jwt Signature"),
    UNSUPPORTED_JWT(false, 5004, "Unsupported Jwt"),
    EMPTY_JWT_CLAIMS_STRING(false, 5005, "Jwt Claims String Empty");

    private final boolean isSuccess;
    private final int returnCode;
    private final String returnMsg;

    BasicServerStatus(boolean isSuccess, int returnCode, String returnMsg){
        this.isSuccess = isSuccess;
        this.returnCode = returnCode;
        this.returnMsg = returnMsg;
    }
}
