package com.swyp8team2.auth.presentation;

import com.swyp8team2.common.presentation.CustomHeader;
import com.swyp8team2.crypto.application.CryptoService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Objects;

@Component
@RequiredArgsConstructor
public class GuestTokenInterceptor implements HandlerInterceptor {

    private final CryptoService cryptoService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
//        String token = request.getHeader(CustomHeader.GUEST_ID);
//        if (Objects.isNull(token)) {
//            return true;
//        }
        return true;
    }
}
