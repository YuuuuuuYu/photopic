package com.swyp8team2.vote.presentation;

import com.swyp8team2.vote.presentation.dto.PostImageVoteStatusResponse;
import com.swyp8team2.support.RestDocsTest;
import com.swyp8team2.support.WithMockUserInfo;
import com.swyp8team2.vote.presentation.dto.VoteRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.JsonFieldType;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.patch;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
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

    @Test
    @WithMockUserInfo
    @DisplayName("게시글 투표 상태 조회")
    void findVoteStatus() throws Exception {
        //given
        var response = List.of(
                new PostImageVoteStatusResponse(1L, "뽀또A", 2, "66.7"),
                new PostImageVoteStatusResponse(2L, "뽀또B", 1, "33.3")
        );
        given(voteService.findVoteStatus(1L, 1L))
                .willReturn(response);

        //when then
        mockMvc.perform(RestDocumentationRequestBuilders.get("/posts/{postId}/votes/status", 1)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer token"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(response)))
                .andDo(restDocs.document(
                        requestHeaders(authorizationHeader()),
                        pathParameters(
                                parameterWithName("postId").description("게시글 Id")
                        ),
                        responseFields(
                                fieldWithPath("[]").type(JsonFieldType.ARRAY).description("투표 선택지 목록"),
                                fieldWithPath("[].id").type(JsonFieldType.NUMBER).description("이미지 Id"),
                                fieldWithPath("[].imageName").type(JsonFieldType.STRING).description("사진 이름"),
                                fieldWithPath("[].voteCount").type(JsonFieldType.NUMBER).description("투표 수"),
                                fieldWithPath("[].voteRatio").type(JsonFieldType.STRING).description("투표 비율")
                        )
                ));
    }
}
