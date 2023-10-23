package com.example.reservation.utils;

import com.example.reservation.exception.ReservationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import static com.example.reservation.exception.ErrorCode.NEED_LOGIN;

public class LoginCheckUtils {

    public static UserDetails loginCheck() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (!(principal instanceof UserDetails)) {
            throw new ReservationException(NEED_LOGIN);
        }

        return (UserDetails) principal;
    }
}
