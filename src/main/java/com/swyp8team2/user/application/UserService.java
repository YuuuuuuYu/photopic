package com.swyp8team2.user.application;

import com.swyp8team2.user.domain.User;
import com.swyp8team2.user.domain.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    @Transactional
    public Long createUser(String email) {
        User user = userRepository.save(User.create("user_" + System.currentTimeMillis(), email));
        return user.getId();
    }
}
