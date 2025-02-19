package com.swyp8team2.auth.presentation;


import com.swyp8team2.auth.application.JwtService;
import com.swyp8team2.auth.application.TokenPair;
import com.swyp8team2.auth.presentation.dto.TokenResponse;
import com.swyp8team2.common.exception.ErrorCode;
import com.swyp8team2.common.exception.UnauthorizedException;
import com.swyp8team2.common.presentation.CustomHeader;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final JwtService jwtService;
    private final RefreshTokenCookieGenerator refreshTokenCookieGenerator;

    @PostMapping("/reissue")
    public ResponseEntity<TokenResponse> reissue(
            @CookieValue(value = CustomHeader.CustomCookie.REFRESH_TOKEN, required = false) String refreshToken,
            HttpServletResponse response
    ) {
        if (!StringUtils.hasText(refreshToken)) {
            throw new UnauthorizedException(ErrorCode.INVALID_TOKEN);
        }
        TokenPair tokenPair = jwtService.reissue(refreshToken);
        Cookie cookie = refreshTokenCookieGenerator.createCookie(tokenPair.refreshToken());
        response.addCookie(cookie);
        return ResponseEntity.ok(new TokenResponse(tokenPair.accessToken()));
    }
}
