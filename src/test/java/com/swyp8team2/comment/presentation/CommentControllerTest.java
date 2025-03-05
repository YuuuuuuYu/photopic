package com.swyp8team2.comment.presentation;

import com.swyp8team2.auth.domain.UserInfo;
import com.swyp8team2.comment.presentation.dto.AuthorDto;
import com.swyp8team2.comment.presentation.dto.CommentResponse;
import com.swyp8team2.comment.presentation.dto.CreateCommentRequest;
import com.swyp8team2.comment.presentation.dto.UpdateCommentRequest;
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
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class CommentControllerTest extends RestDocsTest {

    @Test
    @WithMockUserInfo
    @DisplayName("댓글 생성")
    void createComment() throws Exception {
        //given
        Long postId = 1L;
        CreateCommentRequest request = new CreateCommentRequest("content");

        doNothing().when(commentService).createComment(eq(postId), any(CreateCommentRequest.class), any(UserInfo.class));

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

        verify(commentService, times(1)).createComment(eq(postId), any(CreateCommentRequest.class), any(UserInfo.class));
    }

    @Test
    @WithAnonymousUser
    @DisplayName("댓글 조회")
    void findComments() throws Exception {
        //given
        Long postId = 1L;
        Long cursor = null;
        int size = 10;
        CommentResponse commentResponse = new CommentResponse(
                1L,
                "댓글 내용",
                new AuthorDto(100L, "닉네임", "http://example.com/profile.png"),
                null,
                LocalDateTime.now(),
                false
        );
        List<CommentResponse> commentList = Collections.singletonList(commentResponse);

        CursorBasePaginatedResponse<CommentResponse> response =
                new CursorBasePaginatedResponse<>(null, false, commentList);

        when(commentService.findComments(eq(null), eq(postId), eq(cursor), eq(size))).thenReturn(response);

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
                                fieldWithPath("data[].voteImageId")
                                        .type(JsonFieldType.NUMBER)
                                        .optional()
                                        .description("작성자가 투표한 이미지 Id (투표 없을 시 null)"),
                                fieldWithPath("data[].createdAt")
                                        .type(JsonFieldType.STRING)
                                        .description("댓글 작성일"),
                                fieldWithPath("data[].isAuthor")
                                        .type(JsonFieldType.BOOLEAN)
                                        .description("작성자 여부")
                                )
                ));

        verify(commentService, times(1)).findComments(eq(null), eq(postId), eq(cursor), eq(size));
    }

    @Test
    @WithMockUserInfo
    @DisplayName("댓글 수정")
    void updateComment() throws Exception {
        //given
        UpdateCommentRequest request = new UpdateCommentRequest("수정 댓글");

        //when then
        mockMvc.perform(post("/posts/{postId}/comments/{commentId}", "1", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header(HttpHeaders.AUTHORIZATION, "Bearer token"))
                .andExpect(status().isOk())
                .andDo(restDocs.document(
                        requestHeaders(authorizationHeader()),
                        pathParameters(
                                parameterWithName("postId").description("게시글 Id"),
                                parameterWithName("commentId").description("댓글 Id")
                        ),
                        requestFields(
                                fieldWithPath("content")
                                        .type(JsonFieldType.STRING)
                                        .description("댓글 내용")
                                        .attributes(constraints("최대 ?글자"))
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
