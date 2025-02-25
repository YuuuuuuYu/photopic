package com.swyp8team2.user.presentation;

import com.swyp8team2.user.application.UserService;
import com.swyp8team2.user.presentation.dto.UserInfoResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    @GetMapping("/{userId}")
    public ResponseEntity<UserInfoResponse> findUserInfo(@PathVariable("userId") Long userId) {
        return ResponseEntity.ok(userService.findById(userId));
    }
}
