package com.swyp8team2.comment.presentation;

import com.swyp8team2.comment.presentation.dto.AuthorDto;
import com.swyp8team2.comment.presentation.dto.CommentResponse;
import com.swyp8team2.comment.presentation.dto.CreateCommentRequest;
import com.swyp8team2.common.dto.CursorBasePaginatedResponse;
import com.swyp8team2.support.RestDocsTest;
import com.swyp8team2.support.WithMockUserInfo;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.security.test.context.support.WithAnonymousUser;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class CommentControllerTest extends RestDocsTest {


    @Test
    @WithMockUserInfo
    @DisplayName("댓글 생성")
    void createComment() throws Exception {
        //given
        CreateCommentRequest request = new CreateCommentRequest("content");

        //when then
        mockMvc.perform(post("/posts/{postId}/comments", "1")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andDo(restDocs.document(
                        requestHeaders(authorizationHeader()),
                        pathParameters(
                                parameterWithName("postId").description("게시글 Id")
                        ),
                        requestFields(
                                fieldWithPath("content").type(JsonFieldType.STRING).description("댓글 내용").attributes(constraints("최대 ?글자"))
                        )
                ));
    }

    @Test
    @WithAnonymousUser
    @DisplayName("댓글 조회")
    void findComments() throws Exception {
        //given
        CursorBasePaginatedResponse<CommentResponse> response = new CursorBasePaginatedResponse<>(
                1L,
                false,
                List.of(
                        new CommentResponse(
                                1L,
                                "content",
                                new AuthorDto(1L, "author", "https://image.com/profile-image"),
                                1L,
                                LocalDateTime.of(2025, 2, 13, 12, 0)
                        )
                )
        );

        //when
        mockMvc.perform(get("/posts/{postId}/comments", "1"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(response)))
                .andDo(restDocs.document(
                        pathParameters(
                                parameterWithName("postId").description("게시글 Id")
                        ),
                        queryParameters(cursorQueryParams()),
                        responseFields(
                                fieldWithPath("nextCursor")
                                        .type(JsonFieldType.NUMBER)
                                        .optional()
                                        .description("다음 조회 커서 값"),
                                fieldWithPath("hasNext")
                                        .type(JsonFieldType.BOOLEAN)
                                        .description("다음 페이지 존재 여부 (기본 값 10)"),
                                fieldWithPath("data[]")
                                        .type(JsonFieldType.ARRAY)
                                        .description("댓글 데이터"),
                                fieldWithPath("data[].commentId")
                                        .type(JsonFieldType.NUMBER)
                                        .description("댓글 Id"),
                                fieldWithPath("data[].content")
                                        .type(JsonFieldType.STRING)
                                        .description("댓글 내용"),
                                fieldWithPath("data[].author")
                                        .type(JsonFieldType.OBJECT)
                                        .description("작성자"),
                                fieldWithPath("data[].author.userId")
                                        .type(JsonFieldType.NUMBER)
                                        .description("작성자 id"),
                                fieldWithPath("data[].author.nickname")
                                        .type(JsonFieldType.STRING)
                                        .description("작성자 닉네임"),
                                fieldWithPath("data[].author.profileUrl")
                                        .type(JsonFieldType.STRING)
                                        .description("작성자 프로필 이미지 url"),
                                fieldWithPath("data[].voteId")
                                        .type(JsonFieldType.NUMBER)
                                        .optional()
                                        .description("작성자 투표 Id (투표 없을 시 null)"),
                                fieldWithPath("data[].createdAt")
                                        .type(JsonFieldType.STRING)
                                        .description("댓글 작성일")
                                )
                ));
    }

    @Test
    @WithMockUserInfo
    @DisplayName("댓글 삭제")
    void deleteComment() throws Exception {
        //given

        //when then
        mockMvc.perform(delete("/posts/{postId}/comments/{commentId}", "1", "1")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer token"))
                .andExpect(status().isOk())
                .andDo(restDocs.document(
                        requestHeaders(authorizationHeader()),
                        pathParameters(
                                parameterWithName("postId").description("게시글 Id"),
                                parameterWithName("commentId").description("댓글 Id")
                        )
                ));
    }
}
