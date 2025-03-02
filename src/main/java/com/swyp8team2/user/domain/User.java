package com.swyp8team2.user.domain;

import com.swyp8team2.common.domain.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

import static com.swyp8team2.common.util.Validator.validateEmptyString;
import static com.swyp8team2.common.util.Validator.validateNull;

@Getter
@Entity
@Table(name = "users")
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nickname;

    private String profileUrl;

    @Enumerated(jakarta.persistence.EnumType.STRING)
    public Role role;

    public User(Long id, String nickname, String profileUrl, Role role) {
        this.id = id;
        this.nickname = nickname;
        this.profileUrl = profileUrl;
        this.role = role;
    }

    public static User create(String nickname, String profileUrl) {
        return new User(null, nickname, profileUrl, Role.USER);
    }

    public static User createGuest() {
        return new User(
                null,
                "guest_" + System.currentTimeMillis(),
                "https://image.photopic.site/images-dev/resized_202502240006030.png",
                Role.GUEST
        );
    }
}
