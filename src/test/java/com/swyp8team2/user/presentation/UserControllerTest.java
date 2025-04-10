package com.swyp8team2.user.presentation;

import com.swyp8team2.support.RestDocsTest;
import com.swyp8team2.support.WithMockUserInfo;
import com.swyp8team2.user.domain.Role;
import com.swyp8team2.user.presentation.dto.UserInfoResponse;
import com.swyp8team2.user.presentation.dto.UserMyInfoResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.security.test.context.support.WithMockUser;

import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.payload.JsonFieldType.NUMBER;
import static org.springframework.restdocs.payload.JsonFieldType.STRING;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class UserControllerTest extends RestDocsTest {

    @Test
    @WithMockUserInfo
    @DisplayName("유저 정보 조회")
    void findUserInfo() throws Exception {
        //given
        UserInfoResponse response = new UserInfoResponse(1L, "nickname", "https://image.com/profile-image");
        given(userService.findById(1L))
                .willReturn(response);

        //when then
        mockMvc.perform(RestDocumentationRequestBuilders.get("/users/{userId}", "1"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(response)))
                .andDo(restDocs.document(
                        pathParameters(
                                parameterWithName("userId").description("유저 아이디")
                        ),
                        responseFields(
                                fieldWithPath("id").description("유저 아이디").type(NUMBER),
                                fieldWithPath("nickname").description("닉네임").type(STRING),
                                fieldWithPath("profileUrl").description("프로필 이미지 URL").type(STRING)
                        )
                ));
    }

    @Test
    @WithMockUserInfo
    @DisplayName("본인 정보 조회")
    void findMe() throws Exception {
        //given
        UserMyInfoResponse response = new UserMyInfoResponse(1L, "nickname", "https://image.com/profile-image", Role.USER);
        given(userService.findByMe(1L))
                .willReturn(response);

        //when then
        mockMvc.perform(RestDocumentationRequestBuilders.get("/users/me")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer access-token"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(response)))
                .andDo(restDocs.document(
                        requestHeaders(authorizationHeader()),
                        responseFields(
                                fieldWithPath("id").description("유저 아이디").type(NUMBER),
                                fieldWithPath("nickname").description("닉네임").type(STRING),
                                fieldWithPath("profileImageUrl").description("프로필 이미지 URL").type(STRING),
                                fieldWithPath("role").description("유저 권한").type(STRING)
                        )
                ));
    }
}
