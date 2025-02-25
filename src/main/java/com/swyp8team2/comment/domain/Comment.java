package com.swyp8team2.comment.domain;

import com.swyp8team2.common.domain.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.GenerationType;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static com.swyp8team2.common.util.Validator.validateEmptyString;
import static com.swyp8team2.common.util.Validator.validateNull;

@Entity
@Getter
@Table(name = "comments")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Comment extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private Long postId;

    @NotNull
    private Long userNo;

    @NotNull
    private String content;

    public Comment(Long id, Long postId, Long userNo, String content) {
        validateNull(postId, userNo);
        validateEmptyString(content);
        this.id = id;
        this.postId = postId;
        this.userNo = userNo;
        this.content = content;
    }

    public Comment(Long postId, Long userNo, String content) {
        validateNull(postId, userNo);
        validateEmptyString(content);
        this.postId = postId;
        this.userNo = userNo;
        this.content = content;
    }
}
