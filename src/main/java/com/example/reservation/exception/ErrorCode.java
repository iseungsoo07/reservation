package com.example.reservation.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    NOT_FOUND_MEMBER("사용자 정보가 없습니다."),
    PASSWORD_UNMATCH("사용자 정보가 없습니다."),
    ALREADY_USING_ID("이미 사용중인 아이디 입니다.");

    private final String description;
}
