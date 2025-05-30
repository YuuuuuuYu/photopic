package com.swyp8team2.auth.domain;

import com.swyp8team2.auth.application.oauth.dto.OAuthUserInfo;
import com.swyp8team2.common.domain.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static com.swyp8team2.common.util.Validator.validateEmptyString;
import static com.swyp8team2.common.util.Validator.validateNull;

@Getter
@Entity
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
public class SocialAccount extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;

    @Column(nullable = false, unique = true)
    private String socialId;

    @Enumerated(EnumType.STRING)
    private Provider provider;

    public SocialAccount(Long id, Long userId, String socialId, Provider provider) {
        validateNull(userId, provider);
        validateEmptyString(socialId);
        this.id = id;
        this.userId = userId;
        this.socialId = socialId;
        this.provider = provider;
    }

    public static SocialAccount create(Long userId, OAuthUserInfo oAuthUserInfo) {
        validateNull(oAuthUserInfo);
        return new SocialAccount(null, userId, oAuthUserInfo.socialId(), oAuthUserInfo.provider());
    }
}
