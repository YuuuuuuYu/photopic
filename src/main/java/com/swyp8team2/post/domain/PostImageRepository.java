package com.swyp8team2.post.domain;

import com.swyp8team2.post.presentation.dto.PostImageResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostImageRepository extends JpaRepository<PostImage, Long> {

    @Query("""
            SELECT new com.swyp8team2.post.presentation.dto.PostImageResponse(
                    pi.id,
                    pi.name,
                    i.imageUrl,
                    i.thumbnailUrl,
                    (SELECT v.id FROM Vote v WHERE v.postImageId = pi.id AND v.userId = :userId)
            )
            FROM PostImage pi
            INNER JOIN ImageFile i ON pi.imageFileId = i.id
            WHERE pi.post.id = :postId 
            ORDER BY pi.id ASC
            """
    )
    List<PostImageResponse> findByPostId(@Param("userId") Long userId, @Param("postId") Long postId);
}
