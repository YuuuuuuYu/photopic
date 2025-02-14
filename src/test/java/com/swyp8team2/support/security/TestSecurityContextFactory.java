package com.swyp8team2.support.security;

import com.swyp8team2.auth.domain.UserInfo;
import com.swyp8team2.support.WithMockUserInfo;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

import java.util.Collections;

public class TestSecurityContextFactory implements WithSecurityContextFactory<WithMockUserInfo> {

    @Override
    public SecurityContext createSecurityContext(WithMockUserInfo annotation) {
        long userId = annotation.userId();
        UserInfo userInfo = new UserInfo(userId);

        Authentication auth = new UsernamePasswordAuthenticationToken(
                userInfo,
                null,
                Collections.emptyList()
        );
        SecurityContext context = SecurityContextHolder.getContext();
        context.setAuthentication(auth);
        return context;
    }
}
