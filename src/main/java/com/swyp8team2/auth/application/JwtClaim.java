package com.swyp8team2.auth.application;

public class JwtClaim {

    public static final String ID = "id";

    private final String id;

    public JwtClaim(long id) {
        this.id = String.valueOf(id);
    }

    public static JwtClaim from(long id) {
        return new JwtClaim(id);
    }

    public Long idAsLong() {
        return Long.parseLong(id);
    }

    public String id() {
        return id;
    }
}