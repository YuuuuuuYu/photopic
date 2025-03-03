package com.swyp8team2.auth.application.jwt;

import com.swyp8team2.user.domain.Role;

public class JwtClaim {

    public static final String ID = "id";
    public static final String ROLE = "role";

    private final String id;
    private final Role role;

    public JwtClaim(long id, Role role) {
        this.id = String.valueOf(id);
        this.role = role;
    }

    public static JwtClaim from(long id, Role role) {
        return new JwtClaim(id, role);
    }

    public Long idAsLong() {
        return Long.parseLong(id);
    }

    public String id() {
        return id;
    }

    public Role role() {
        return role;
    }
}