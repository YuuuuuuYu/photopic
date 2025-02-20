package com.swyp8team2.auth.presentation;

import com.swyp8team2.auth.presentation.dto.TokenResponse;
import com.swyp8team2.common.presentation.CustomHeader;
import com.swyp8team2.support.RestDocsTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.test.context.support.WithAnonymousUser;

import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
class AuthControllerTest extends RestDocsTest {

    @Test
    @WithAnonymousUser
    @DisplayName("토큰 재발급")
    void reissue() throws Exception {
        //given
        TokenResponse response = new TokenResponse("accessToken", "refreshToken");

        //when then
        mockMvc.perform(post("/auth/reissue")
                        .header(CustomHeader.AUTHORIZATION_REFRESH, "refreshToken"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(response)))
                .andDo(restDocs.document(
                        requestHeaders(
                                headerWithName(CustomHeader.AUTHORIZATION_REFRESH).description("리프레시 토큰")
                        ),
                        responseFields(
                                fieldWithPath("accessToken").description("액세스 토큰"),
                                fieldWithPath("refreshToken").description("리프레시 토큰")
                        )
                ));
    }
}
