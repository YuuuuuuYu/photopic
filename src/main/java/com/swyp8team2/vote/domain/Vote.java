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

    private String userSeq;

    public Vote(Long id, Long postId, Long postImageId, String userSeq) {
        this.id = id;
        this.postId = postId;
        this.postImageId = postImageId;
        this.userSeq = userSeq;
    }

    public static Vote of(Long postId, Long postImageId, String userSeq) {
        return new Vote(null, postId, postImageId, userSeq);
    }
}
