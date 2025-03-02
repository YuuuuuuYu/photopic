package com.swyp8team2.user.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.GenerationType;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "nickname_adjectives")
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
public class NicknameAdjective {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String adjective;

    public NicknameAdjective(String adjective) {
        this.adjective = adjective;
    }
}
