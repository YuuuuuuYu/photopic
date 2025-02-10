package com.swyp8team2.user.presentation;

import com.swyp8team2.user.presentation.dto.UserInfoResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {

    @GetMapping("/me")
    public ResponseEntity<UserInfoResponse> findUserInfo() {
        return ResponseEntity.ok(new UserInfoResponse(1L, "nickname", "profileUrl", "email@email.email"));
    }
}
