package com.swyp8team2.auth.application.jwt;

import com.swyp8team2.auth.domain.RefreshToken;
import com.swyp8team2.auth.domain.RefreshTokenRepository;
import com.swyp8team2.common.exception.BadRequestException;
import com.swyp8team2.common.exception.ErrorCode;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class JwtService {

    private final JwtProvider jwtProvider;
    private final RefreshTokenRepository refreshTokenRepository;

    @Transactional
    public TokenPair createToken(long userId) {
        TokenPair tokenPair = jwtProvider.createToken(new JwtClaim(userId));
        RefreshToken refreshToken = refreshTokenRepository.findByUserId(userId)
                .orElseGet(() -> new RefreshToken(userId, tokenPair.refreshToken()));
        refreshToken.setRefreshToken(tokenPair.refreshToken());
        refreshTokenRepository.save(refreshToken);
        return tokenPair;
    }

    @Transactional
    public TokenPair reissue(String refreshToken) {
        JwtClaim claim = jwtProvider.parseToken(refreshToken);
        RefreshToken findRefreshToken = refreshTokenRepository.findByUserId(claim.idAsLong())
                .orElseThrow(() -> new BadRequestException(ErrorCode.REFRESH_TOKEN_NOT_FOUND));

        TokenPair tokenPair = jwtProvider.createToken(new JwtClaim(claim.idAsLong()));
        findRefreshToken.rotate(refreshToken, tokenPair.refreshToken());
        return tokenPair;
    }
}
