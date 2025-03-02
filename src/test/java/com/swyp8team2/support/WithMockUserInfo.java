package com.swyp8team2.support;

import com.swyp8team2.support.security.TestSecurityContextFactory;
import com.swyp8team2.user.domain.Role;
import org.springframework.security.test.context.support.WithSecurityContext;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
@WithSecurityContext(factory = TestSecurityContextFactory.class)
public @interface WithMockUserInfo {
    long userId() default 1L;
    Role role() default Role.USER;
}
