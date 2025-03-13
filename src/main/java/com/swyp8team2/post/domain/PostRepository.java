package com.swyp8team2.post.domain;

import com.swyp8team2.post.presentation.dto.FeedDto;
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

    @Query(""" 
            SELECT new com.swyp8team2.post.presentation.dto.FeedDto(
                    p.id,
                   	p.status ,
                   	p.description ,
                   	p.shareUrl ,
                   	p.userId ,
                   	u.nickname, 
                   	u.profileUrl,
                   	cast((select count(distinct v.id) from Vote v where p.id = v.postId) as long),
                   	cast((select count(*) from Comment c where p.id = c.postId and c.deleted = false) as long)
            )
            FROM Post p
            INNER JOIN User u on p.userId = u.id
            WHERE p.deleted = false
            AND p.scope = 'PUBLIC'
            AND (:postId IS NULL OR p.id < :postId)
            ORDER BY p.createdAt DESC
            """
    )
    Slice<FeedDto> findFeedByScopeWithUser(@Param("userId") Long userId, @Param("postId") Long postId, Pageable pageable);
}
