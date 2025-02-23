package com.swyp8team2.post.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static com.swyp8team2.common.util.Validator.validateEmptyString;
import static com.swyp8team2.common.util.Validator.validateNull;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PostImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Post post;

    private String name;

    private Long imageFileId;

    private int voteCount;

    public PostImage(Long id, Post post, String name, Long imageFileId, int voteCount) {
        validateNull(post, imageFileId);
        validateEmptyString(name);
        this.id = id;
        this.post = post;
        this.name = name;
        this.imageFileId = imageFileId;
        this.voteCount = voteCount;
    }

    public PostImage(String name, Long imageFileId, int voteCount) {
        validateNull(imageFileId);
        validateEmptyString(name);
        this.name = name;
        this.imageFileId = imageFileId;
        this.voteCount = voteCount;
    }

    public static PostImage create(String name, Long imageFileId) {
        return new PostImage(name, imageFileId, 0);
    }

    public void setPost(Post post) {
        validateNull(post);
        this.post = post;
    }
}
