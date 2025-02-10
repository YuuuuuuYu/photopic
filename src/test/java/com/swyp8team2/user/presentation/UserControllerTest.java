package com.swyp8team2.user.presentation;

import com.swyp8team2.support.RestDocsTest;
import com.swyp8team2.user.presentation.dto.UserInfoResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.test.context.support.WithMockUser;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.payload.JsonFieldType.NUMBER;
import static org.springframework.restdocs.payload.JsonFieldType.STRING;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class UserControllerTest extends RestDocsTest {

    @Test
    @WithMockUser
    @DisplayName("유저 정보 조회")
    void findUserInfo() throws Exception {
        //given
        UserInfoResponse response = new UserInfoResponse(1L, "nickname", "profileUrl", "email@email.email");

        //when then
        mockMvc.perform(get("/api/users/me"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(response)))
                .andDo(restDocs.document(
                        responseFields(
                                fieldWithPath("userId").description("유저 아이디").type(NUMBER),
                                fieldWithPath("nickname").description("닉네임").type(STRING),
                                fieldWithPath("profileUrl").description("프로필 이미지 URL").type(STRING),
                                fieldWithPath("email").description("이메일").type(STRING)
                        )
                ));
    }
}
