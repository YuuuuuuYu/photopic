package com.swyp8team2.post.domain;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

    @Query("""
            SELECT p
            FROM Post p
            WHERE p.userId = :userId
            AND (:postId IS NULL OR p.id < :postId)
            ORDER BY p.id DESC
            """
    )
    Slice<Post> findByUserId(@Param("userId") Long userId, @Param("postId") Long postId, Pageable pageable);

    @Query("""
            SELECT p
            FROM Post p
            WHERE p.id IN :postIds
            AND (:postId IS NULL OR p.id < :postId)
            ORDER BY p.id DESC
            """
    )
    Slice<Post> findByIdIn(@Param("postIds") List<Long> postIds, @Param("postId") Long postId, Pageable pageable);

    @Query("""
            SELECT p
            FROM Post p
            JOIN FETCH p.images
            WHERE p.id = :postId
            """
    )
    Optional<Post> findByIdFetchPostImage(@Param("postId") Long postId);
}
