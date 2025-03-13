package com.swyp8team2.auth.application.jwt;

import com.swyp8team2.auth.domain.RefreshToken;
import com.swyp8team2.auth.domain.RefreshTokenRepository;
import com.swyp8team2.auth.presentation.dto.TokenResponse;
import com.swyp8team2.common.exception.BadRequestException;
import com.swyp8team2.common.exception.ErrorCode;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class JwtService {

    private final JwtProvider jwtProvider;
    private final RefreshTokenRepository refreshTokenRepository;

    @Transactional
    public TokenResponse createToken(JwtClaim claim) {
        TokenPair tokenPair = jwtProvider.createToken(claim);
        RefreshToken refreshToken = refreshTokenRepository.findByUserId(claim.idAsLong())
                .orElseGet(() -> new RefreshToken(claim.idAsLong(), tokenPair.refreshToken()));
        refreshToken.setRefreshToken(tokenPair.refreshToken());
        refreshTokenRepository.save(refreshToken);

        return new TokenResponse(tokenPair, claim.idAsLong(), claim.role());
    }

    @Transactional
    public TokenResponse reissue(String refreshToken) {
        JwtClaim claim = jwtProvider.parseToken(refreshToken);
        RefreshToken findRefreshToken = refreshTokenRepository.findByUserId(claim.idAsLong())
                .orElseThrow(() -> new BadRequestException(ErrorCode.REFRESH_TOKEN_NOT_FOUND));

        TokenPair tokenPair = jwtProvider.createToken(new JwtClaim(claim.idAsLong(), claim.role()));
        findRefreshToken.rotate(refreshToken, tokenPair.refreshToken());

        return new TokenResponse(tokenPair, claim.idAsLong(), claim.role());
    }

    @Transactional
    public void signOut(Long userId, String refreshToken) {
        RefreshToken token = refreshTokenRepository.findByUserId(userId)
                .orElseThrow(() -> new BadRequestException(ErrorCode.REFRESH_TOKEN_NOT_FOUND));

        if (!token.getToken().equals(refreshToken)) {
            throw new BadRequestException(ErrorCode.REFRESH_TOKEN_MISMATCHED);
        }
        refreshTokenRepository.delete(token);
    }
}
