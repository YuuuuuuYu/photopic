package com.swyp8team2.auth.presentation;


import com.swyp8team2.auth.presentation.dto.TokenResponse;
import com.swyp8team2.common.presentation.CustomHeader;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    @PostMapping("/reissue")
    public ResponseEntity<TokenResponse> reissue(
            @RequestHeader(CustomHeader.AUTHORIZATION_REFRESH) String refreshToken
    ) {
        return ResponseEntity.ok(new TokenResponse("accessToken", "refreshToken"));
    }
}
