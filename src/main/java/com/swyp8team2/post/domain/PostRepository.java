package com.swyp8team2.post.domain;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

    @Query("""
            SELECT p
            FROM Post p
            WHERE p.userId = :userId
            AND (:postId IS NULL OR p.id < :postId)
            ORDER BY p.createdAt DESC
            """
    )
    Slice<Post> findByUserId(@Param("userId") Long userId, @Param("postId") Long postId, Pageable pageable);
}
