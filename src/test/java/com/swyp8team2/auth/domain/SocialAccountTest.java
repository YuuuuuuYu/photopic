package com.swyp8team2.auth.domain;

import com.swyp8team2.common.exception.ErrorCode;
import com.swyp8team2.common.exception.InternalServerException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

class SocialAccountTest {


    @Test
    @DisplayName("SocialAccount Entity 생성")
    void create() throws Exception {
        //given
        long givenUserId = 1L;
        String givenSocialId = "socialId";
        Provider givenProvider = Provider.KAKAO;

        //when
        SocialAccount socialAccount = SocialAccount.create(givenUserId, givenSocialId, givenProvider);

        //then
        assertAll(
                () -> assertThat(socialAccount.getUserId()).isEqualTo(givenUserId),
                () -> assertThat(socialAccount.getSocialId()).isEqualTo(givenSocialId),
                () -> assertThat(socialAccount.getProvider()).isEqualTo(givenProvider)
        );
    }

    @Test
    @DisplayName("SocialAccount Entity 생성 - 파라미터가 null인 경우")
    void create_null() throws Exception {
        //given

        //when then
        assertAll(
                () -> assertThatThrownBy(() -> SocialAccount.create(1L, null, Provider.KAKAO))
                        .isInstanceOf(InternalServerException.class)
                        .hasMessage(ErrorCode.INVALID_INPUT_VALUE.getMessage()),
                () -> assertThatThrownBy(() -> SocialAccount.create(1L, "socialId", null))
                        .isInstanceOf(InternalServerException.class)
                        .hasMessage(ErrorCode.INVALID_INPUT_VALUE.getMessage())
        );
    }

    @Test
    @DisplayName("SocialAccount Entity 생성 - socialId가 빈 문자인 경우")
    void create_emptyString() throws Exception {
        //given

        //when then
        assertThatThrownBy(() -> SocialAccount.create(1L, "", Provider.KAKAO))
                .isInstanceOf(InternalServerException.class)
                .hasMessage(ErrorCode.INVALID_INPUT_VALUE.getMessage());
    }
}
