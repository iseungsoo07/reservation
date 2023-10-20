package com.example.reservation.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    INTERNAL_SERVER_ERROR("내부 서버 오류"),
    INVALID_REQUEST("잘못된 요청입니다."),
    ACCESS_DENIED("권한이 없습니다."),

    // 사용자 관련
    NOT_FOUND_MEMBER("사용자 정보가 없습니다."),
    PASSWORD_UNMATCH("비밀번호가 일치하지 않습니다."),
    ALREADY_USING_ID("이미 사용중인 아이디 입니다."),

    // 매장 관련
    ALREADY_EXISTS_STORE("이미 등록되어 있는 매장입니다.");

    private final String description;
}
