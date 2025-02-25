package com.swyp8team2.vote.presentation;

import com.swyp8team2.common.presentation.CustomHeader;
import com.swyp8team2.support.RestDocsTest;
import com.swyp8team2.support.WithMockUserInfo;
import com.swyp8team2.vote.presentation.dto.ChangeVoteRequest;
import com.swyp8team2.vote.presentation.dto.VoteRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.security.test.context.support.WithAnonymousUser;

import java.util.UUID;

import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.patch;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class VoteControllerTest extends RestDocsTest {

    @Test
    @WithMockUserInfo
    @DisplayName("투표")
    void vote() throws Exception {
        //given
        VoteRequest request = new VoteRequest(1L);

        //when test
        mockMvc.perform(post("/posts/{postId}/votes", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header(HttpHeaders.AUTHORIZATION, "Bearer token"))
                .andExpect(status().isOk())
                .andDo(restDocs.document(
                        requestHeaders(authorizationHeader()),
                        pathParameters(
                                parameterWithName("postId").description("게시글 Id")
                        ),
                        requestFields(
                                fieldWithPath("imageId")
                                        .type(JsonFieldType.NUMBER)
                                        .description("투표 후보 Id")
                        )
                ));
    }

    @Test
    @WithAnonymousUser
    @DisplayName("게스트 투표")
    void guestVote() throws Exception {
        //given
        VoteRequest request = new VoteRequest(1L);

        //when test
        mockMvc.perform(post("/posts/{postId}/votes/guest", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header(CustomHeader.GUEST_ID, UUID.randomUUID().toString()))
                .andExpect(status().isOk())
                .andDo(restDocs.document(
                        requestHeaders(guestHeader()),
                        pathParameters(
                                parameterWithName("postId").description("게시글 Id")
                        ),
                        requestFields(
                                fieldWithPath("imageId")
                                        .type(JsonFieldType.NUMBER)
                                        .description("투표 후보 Id")
                        )
                ));
    }

    @Test
    @WithMockUserInfo
    @DisplayName("투표 변경")
    void changeVote() throws Exception {
        //given
        ChangeVoteRequest request = new ChangeVoteRequest(1L);

        //when
        mockMvc.perform(patch("/posts/{postId}/votes", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header(HttpHeaders.AUTHORIZATION, "Bearer token"))
                .andExpect(status().isOk())
                .andDo(restDocs.document(
                        requestHeaders(authorizationHeader()),
                        pathParameters(
                                parameterWithName("postId").description("변경할 게시글 Id")
                        ),
                        requestFields(
                                fieldWithPath("imageId")
                                        .type(JsonFieldType.NUMBER)
                                        .description("변경할 투표 이미지 Id")
                        )
                ));
    }

    @Test
    @WithAnonymousUser
    @DisplayName("게스트 투표 변경")
    void guestChangeVote() throws Exception {
        //given
        ChangeVoteRequest request = new ChangeVoteRequest(1L);

        //when
        mockMvc.perform(patch("/posts/{postId}/votes/guest", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header(CustomHeader.GUEST_ID, UUID.randomUUID().toString()))
                .andExpect(status().isOk())
                .andDo(restDocs.document(
                        requestHeaders(guestHeader()),
                        pathParameters(
                                parameterWithName("postId").description("변경활 게시글 Id")
                        ),
                        requestFields(
                                fieldWithPath("imageId")
                                        .type(JsonFieldType.NUMBER)
                                        .description("변경할 투표 이미지 Id")
                        )
                ));
    }
}
