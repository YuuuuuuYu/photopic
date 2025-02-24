package com.swyp8team2.support;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.swyp8team2.auth.application.AuthService;
import com.swyp8team2.auth.presentation.RefreshTokenCookieGenerator;
import com.swyp8team2.comment.application.CommentService;
import com.swyp8team2.image.application.ImageService;
import com.swyp8team2.post.application.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest
@Import(RefreshTokenCookieGenerator.class)
public abstract class WebUnitTest {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @Autowired
    protected RefreshTokenCookieGenerator refreshTokenCookieGenerator;

    @MockitoBean
    protected AuthService authService;

    @MockitoBean
    protected ImageService imageService;

    @MockitoBean
    protected PostService postService;

    @MockitoBean
    protected CommentService commentService;
}
