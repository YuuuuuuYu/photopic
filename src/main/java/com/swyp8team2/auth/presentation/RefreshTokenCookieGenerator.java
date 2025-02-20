package com.swyp8team2.auth.presentation;

import com.swyp8team2.common.presentation.CustomHeader;
import jakarta.servlet.http.Cookie;
import org.springframework.stereotype.Component;

@Component
public class RefreshTokenCookieGenerator {

    public Cookie createCookie(String refreshToken) {
        Cookie cookie = new Cookie(CustomHeader.CustomCookie.REFRESH_TOKEN, refreshToken);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setAttribute("SameSite", "None");
        cookie.setPath("/auth/reissue");
        cookie.setMaxAge(60 * 60 * 24 * 14);
        return cookie;
    }
}
