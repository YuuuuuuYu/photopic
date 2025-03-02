package com.swyp8team2.comment.domain;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    @Query("""
            SELECT c
            FROM Comment c
            WHERE c.postId = :postId
                AND (:cursor is null or c.id > :cursor)
            ORDER BY c.createdAt ASC
            """)
    Slice<Comment> findByPostId(
            @Param("postId") Long postId,
            @Param("cursor") Long cursor,
            Pageable pageable
    );

}
