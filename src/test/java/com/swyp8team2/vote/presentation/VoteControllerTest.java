package com.swyp8team2.vote.presentation;

import com.swyp8team2.common.presentation.CustomHeader;
import com.swyp8team2.support.RestDocsTest;
import com.swyp8team2.support.WithMockUserInfo;
import com.swyp8team2.user.domain.Role;
import com.swyp8team2.vote.presentation.dto.ChangeVoteRequest;
import com.swyp8team2.vote.presentation.dto.VoteRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
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
        verify(voteService, times(1)).vote(any(), any(), any());
    }

    @Test
    @WithMockUserInfo
    @DisplayName("투표 취소")
    void cancelVote() throws Exception {
        //given

        //when test
        mockMvc.perform(delete("/votes/{voteId}", "1")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer token"))
                .andExpect(status().isOk())
                .andDo(restDocs.document(
                        requestHeaders(authorizationHeader()),
                        pathParameters(
                                parameterWithName("voteId").description("투표 Id")
                        )
                ));
        verify(voteService, times(1)).cancelVote(any(), any());
    }
}
