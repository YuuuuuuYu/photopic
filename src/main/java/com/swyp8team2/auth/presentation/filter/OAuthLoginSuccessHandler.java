package com.swyp8team2.auth.presentation.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.swyp8team2.auth.application.JwtClaim;
import com.swyp8team2.auth.application.JwtProvider;
import com.swyp8team2.auth.application.JwtService;
import com.swyp8team2.auth.application.TokenPair;
import com.swyp8team2.auth.domain.OAuthUser;
import com.swyp8team2.auth.presentation.RefreshTokenCookieGenerator;
import com.swyp8team2.auth.presentation.dto.TokenResponse;
import com.swyp8team2.common.presentation.CustomHeader;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Component
@RequiredArgsConstructor
public class OAuthLoginSuccessHandler implements AuthenticationSuccessHandler {

    private final ObjectMapper objectMapper;
    private final JwtService jwtService;
    private final RefreshTokenCookieGenerator refreshTokenCookieGenerator;

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) throws IOException {
        OAuthUser oAuthUser = (OAuthUser) authentication.getPrincipal();
        TokenPair tokenPair = jwtService.createToken(oAuthUser.getUserId());

        Cookie cookie = refreshTokenCookieGenerator.createCookie(tokenPair.refreshToken());

        response.addCookie(cookie);
        response.setContentType(APPLICATION_JSON_VALUE);
        response.setStatus(HttpServletResponse.SC_OK);
        response.getWriter().write(objectMapper.writeValueAsString(new TokenResponse(tokenPair.accessToken())));
        response.getWriter().flush();
    }
}
