package com.swyp8team2.auth.presentation.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.swyp8team2.auth.application.JwtClaim;
import com.swyp8team2.auth.application.JwtProvider;
import com.swyp8team2.auth.application.TokenPair;
import com.swyp8team2.auth.domain.OAuthUser;
import com.swyp8team2.auth.presentation.dto.TokenResponse;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Component
@RequiredArgsConstructor
public class OAuthLoginSuccessHandler implements AuthenticationSuccessHandler {

    private final JwtProvider jwtProvider;
    private final ObjectMapper objectMapper;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        OAuthUser oAuthUser = (OAuthUser) authentication.getPrincipal();

        TokenPair tokenPair = jwtProvider.createToken(JwtClaim.from(oAuthUser.getUserId()));

        response.setContentType(APPLICATION_JSON_VALUE);
        response.setStatus(HttpServletResponse.SC_OK);

        TokenResponse tokenResponse = new TokenResponse(tokenPair.accessToken(), tokenPair.refreshToken());
        response.getWriter().write(objectMapper.writeValueAsString(tokenResponse));
        response.getWriter().flush();
    }
}
