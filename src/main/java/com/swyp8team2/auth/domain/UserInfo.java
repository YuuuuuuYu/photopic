package com.swyp8team2.auth.domain;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

import static com.swyp8team2.common.util.Validator.validateNull;

public record UserInfo(long userId) implements UserDetails {

    public UserInfo {
        validateNull(userId);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.emptyList();
    }

    @Override
    public String getPassword() {
        return "";
    }

    @Override
    public String getUsername() {
        return String.valueOf(userId);
    }
}
