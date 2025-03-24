package com.swyp8team2.user.application;

import com.swyp8team2.user.domain.NicknameAdjectiveRepository;
import com.swyp8team2.user.domain.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NicknameGenerator {

    private final NicknameAdjectiveRepository nicknameAdjectiveRepository;

    public String generate(Role role) {
        long randomIndex = (long)(Math.random() * 500);
        return nicknameAdjectiveRepository.findNicknameAdjectiveById(randomIndex)
                .map(adjective -> adjective.getAdjective() + " " + role.getNickname())
                .orElse("숨겨진 " + role.getNickname());
    }
}
