package com.swyp8team2.auth.application;

import com.swyp8team2.auth.application.jwt.JwtClaim;
import com.swyp8team2.user.domain.Role;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class JwtClaimTest {

    @Test
    @DisplayName("JwtClaim 생성")
    void idAsLong() {
        // given
        long givenId = 1;
        Role givenRole = Role.GUEST;

        // when
        JwtClaim jwtClaim = JwtClaim.from(givenId, givenRole);

        // then
        assertThat(jwtClaim.idAsLong()).isEqualTo(givenId);
        assertThat(jwtClaim.role()).isEqualTo(givenRole);
    }
}
