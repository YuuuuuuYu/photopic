package com.swyp8team2.auth.domain;

import com.swyp8team2.common.exception.BadRequestException;
import com.swyp8team2.common.exception.ErrorCode;
import com.swyp8team2.common.util.Validator;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;

    private String token;

    public RefreshToken(Long userId, String token) {
        Validator.validateNull(userId);
        Validator.validateEmptyString(token);
        this.userId = userId;
        this.token = token;
    }

    public void rotate(String currentToken, String newToken) {
        if (!this.token.equals(currentToken)) {
            throw new BadRequestException(ErrorCode.REFRESH_TOKEN_MISMATCHED);
        }
        setRefreshToken(newToken);
    }

    public void setRefreshToken(String token) {
        Validator.validateEmptyString(token);
        this.token = token;
    }
}
