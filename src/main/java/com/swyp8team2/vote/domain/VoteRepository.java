package com.swyp8team2.vote.domain;

import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VoteRepository extends JpaRepository<Vote, Long> {
    Optional<Vote> findByUserSeqAndPostId(String userSeq, Long postId);

    Slice<Vote> findByUserSeq(String seq);
}
