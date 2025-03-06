package com.swyp8team2.comment.domain;

import com.swyp8team2.support.RepositoryTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class CommentRepositoryTest extends RepositoryTest {

    @Autowired
    private CommentRepository commentRepository;

    @Test
    @DisplayName("댓글 조회")
    void select_CommentUser() {
        // given
        Comment comment1 = new Comment(1L, 100L, "content1");
        Comment comment2 = new Comment(1L, 101L, "content2");
        Comment comment3 = new Comment(1L, 102L, "content3");
        commentRepository.saveAll(List.of(comment1, comment2, comment3));

        // when
        Slice<Comment> result1 = commentRepository.findByPostId(1L, null, PageRequest.of(0, 10));

        // then
        assertThat(result1.getContent()).hasSize(3);
    }

    @Test
    @DisplayName("댓글 조회 - 단일 조회")
    void select_CommentById() {
        // given
        Comment comment = new Comment(1L, 100L, "content");
        commentRepository.save(comment);

        // when
        Comment selectComment = commentRepository.findByIdAndNotDeleted(1L)
                .orElse(new Comment(2L, 2L, 101L, "content"));

        // then
        assertThat(comment.getId()).isEqualTo(selectComment.getId());
    }
}