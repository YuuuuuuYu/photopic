package com.swyp8team2.auth.application;

import com.swyp8team2.auth.application.jwt.JwtClaim;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class JwtClaimTest {

    @Test
    @DisplayName("JwtClaim 생성")
    void idAsLong() {
        // given
        long givenId = 1;

        // when
        JwtClaim jwtClaim = JwtClaim.from(givenId);

        // then
        Assertions.assertThat(jwtClaim.idAsLong()).isEqualTo(givenId);
    }
}
