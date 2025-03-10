package com.swyp8team2.vote.domain;

import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VoteRepository extends JpaRepository<Vote, Long> {
    List<Vote> findByUserIdAndPostId(Long userId, Long postId);

    Slice<Vote> findByUserId(Long userId);

    Optional<Vote> findByUserIdAndPostImageId(Long voterId, Long imageId);

    Optional<Vote> findByIdAndUserId(Long voteId, Long userId);

    List<Vote> findByPostIdAndDeletedFalse(Long postId);
}
