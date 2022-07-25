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
    MODIFY_IT_IS_NOT_AUTHOR_ERROR(false, 3054, "삭제를 시도한 유저 자신이 작성한 댓글이 아닙니다."),
    MODIFY_ALREADY_DELETED_COMMENT(false, 3055, "댓글이 삭제되어 수정이 불가합니다."),
    COMMENT_ROOT_POST_NOT_EXIST(false, 3056, "댓글을 조회하려는 게시물이 존재하지 않습니다."),
    // [POST]
    POST_USERS_EMPTY_EMAIL(false, 3020, "이메일을 입력해주세요."),
    POST_USERS_EMPTY_PASSWORD(false, 3021, "비밀번호를 입력해주세요."),
    POST_USERS_INVALID_EMAIL(false, 3022, "이메일 형식을 확인해주세요."),

    // [USER]
    POST_USERS_INCORRECT_PASSWORD(false,3023,"비밀번호가 일치하지 않습니다."),

    POST_USERS_EMPTY_NICKNAME(false, 3024, "닉네임을 입력해주세요."),

    POST_USERS_EMPTY_BIRTHDAY(false, 3025, "생년월일을 입력해주세요."),

    POST_USERS_EXIST_EMAIL(false,3026,"중복된 이메일입니다"),
    POST_USERS_EXIST_NICKNAME(false,3027,"중복된 닉네임입니다"),

    // [BOARD]
    BOARD_NOT_EXIST(false, 3060, "게시판이 존재하지 않습니다."),
    SCRAP_BOARD_EXIST(false, 3061, "이미 스크랩한 게시판 입니다."),
    SCRAP_BOARD_NOT_EXIST(false, 3062, "스크랩하지 않은 게시판 입니다."),

    BOARD_SCRAP_FAIL(false, 3063, "게시판 스크랩에 실패했습니다."),
    BOARD_SCRAP_CANCEL_FAIL(false, 3064, "게시판 스크랩 취소에 실패했습니다."),
    BOARD_INSERT_FAIL(false, 3065, "스크랩 게시판에 생성에 실패했습니다"),


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

    /**
     * 6000: 신고 API에서 사용 6000 번부터는 하원이 사용 6500번부터 미뇽이 사용
     */

    /**
     * 7000: 커리큘럼 API에서 사용 7000번 부터 찜니가 사용, 7300번 부터 루키가 사용, 7600번 부터 루카가 사용
     * */

    private final boolean isSuccess;
    private final int returnCode;
    private final String returnMsg;

    BasicServerStatus(boolean isSuccess, int returnCode, String returnMsg){
        this.isSuccess = isSuccess;
        this.returnCode = returnCode;
        this.returnMsg = returnMsg;
    }
}
