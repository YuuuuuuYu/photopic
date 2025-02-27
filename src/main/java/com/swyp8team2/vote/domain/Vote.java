package com.swyp8team2.vote.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "user_votes")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Vote {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long postId;

    private Long postImageId;

    private Long userId;

    public Vote(Long id, Long postId, Long postImageId, Long userId) {
        this.id = id;
        this.postId = postId;
        this.postImageId = postImageId;
        this.userId = userId;
    }

    public static Vote of(Long postId, Long postImageId, Long userId) {
        return new Vote(null, postId, postImageId, userId);
    }
}
