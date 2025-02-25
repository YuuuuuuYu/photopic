package com.swyp8team2.user.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface NicknameAdjectiveRepository extends JpaRepository<NicknameAdjective, Long> {

    Optional<NicknameAdjective> findNicknameAdjectiveById(Long id);
}
