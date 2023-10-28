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
    NEED_LOGIN("로그인 후 이용하실 수 있습니다."),
    ONLY_FOR_USER("사용자 권한 필요."),
    ONLY_FOR_PARTNER("파트너 권한 필요."),

    // 매장 관련
    NOT_FOUND_STORE("매장 정보가 없습니다."),
    ALREADY_EXISTS_STORE("이미 등록되어 있는 매장입니다."),

    // 예약 관련
    SERVICE_ONLY_FOR_OWNER("해당 매장의 점장만 이용가능한 서비스입니다."),
    NOT_FOUND_RESERVATION("예약 정보가 없습니다."),
    ALREADY_RESERVED_TIME("이미 예약이 된 시간입니다. 다른 시간을 선택해주세요.");

    private final String description;
}
