package com.swyp8team2.post.presentation;

import com.swyp8team2.common.dto.CursorBasePaginatedResponse;
import com.swyp8team2.post.domain.Scope;
import com.swyp8team2.post.domain.Status;
import com.swyp8team2.post.domain.VoteType;
import com.swyp8team2.post.presentation.dto.*;
import com.swyp8team2.support.RestDocsTest;
import com.swyp8team2.support.WithMockUserInfo;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.security.test.context.support.WithAnonymousUser;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class PostControllerTest extends RestDocsTest {

    @Test
    @WithMockUserInfo
    @DisplayName("게시글 생성")
    void createPost() throws Exception {
        //given
        CreatePostRequest request = new CreatePostRequest(
                "제목",
                List.of(new PostImageRequestDto(1L), new PostImageRequestDto(2L)),
                Scope.PRIVATE,
                VoteType.SINGLE
        );
        CreatePostResponse response = new CreatePostResponse(1L, "shareUrl");
        given(postService.create(any(), any()))
                .willReturn(response);

        //when then
        mockMvc.perform(post("/posts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header(HttpHeaders.AUTHORIZATION, "Bearer token"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(response)))
                .andDo(restDocs.document(
                        requestHeaders(authorizationHeader()),
                        requestFields(
                                fieldWithPath("description")
                                        .type(JsonFieldType.STRING)
                                        .description("설명")
                                        .attributes(constraints("0~100자 사이")),
                                fieldWithPath("images")
                                        .type(JsonFieldType.ARRAY)
                                        .description("투표 후보")
                                        .attributes(constraints("최소 2개")),
                                fieldWithPath("images[].imageFileId")
                                        .type(JsonFieldType.NUMBER)
                                        .description("투표 후보 이미지 ID"),
                                fieldWithPath("scope")
                                        .type(JsonFieldType.STRING)
                                        .description("투표 공개범위 (PRIVATE, PUBLIC)"),
                                fieldWithPath("voteType")
                                        .type(JsonFieldType.STRING)
                                        .description("투표 방식 (SINGLE, MULTIPLE)")
                        ),
                        responseFields(
                                fieldWithPath("postId")
                                        .type(JsonFieldType.NUMBER)
                                        .description("게시글 Id"),
                                fieldWithPath("shareUrl")
                                        .type(JsonFieldType.STRING)
                                        .description("게시글 공유 url")
                        )
                ));
    }
    
    @Test
    @WithAnonymousUser
    @DisplayName("게시글 공유 url 상세 조회")
    void findPost_shareUrl() throws Exception {
        //given
        PostResponse response = new PostResponse(
                1L,
                new AuthorDto(
                        1L,
                        "author",
                        "https://image.photopic.site/profile-image"
                ),
                "description",
                List.of(
                        new PostImageResponse(1L, "뽀또A", "https://image.photopic.site/image/1", "https://image.photopic.site/image/resize/1", 1L),
                        new PostImageResponse(2L, "뽀또B", "https://image.photopic.site/image/2", "https://image.photopic.site/image/resize/2", null)
                ),
                "https://photopic.site/shareurl",
                true,
                Status.PROGRESS,
                LocalDateTime.of(2025, 2, 13, 12, 0)
        );
        given(postService.findByShareUrl(any(), any()))
                .willReturn(response);

        //when then
        mockMvc.perform(RestDocumentationRequestBuilders.get("/posts/shareUrl/{shareUrl}", "JNOfBVfcG2z89afSiRrOyQ"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(response)))
                .andDo(restDocs.document(
                        pathParameters(
                                parameterWithName("shareUrl").description("공유 url")
                        ),
                        responseFields(
                                fieldWithPath("id").type(JsonFieldType.NUMBER).description("게시글 Id"),
                                fieldWithPath("author").type(JsonFieldType.OBJECT).description("게시글 작성자 정보"),
                                fieldWithPath("author.id").type(JsonFieldType.NUMBER).description("게시글 작성자 유저 Id"),
                                fieldWithPath("author.nickname").type(JsonFieldType.STRING).description("게시글 작성자 닉네임"),
                                fieldWithPath("author.profileUrl").type(JsonFieldType.STRING).description("게시글 작성자 프로필 이미지"),
                                fieldWithPath("description").type(JsonFieldType.STRING).description("설명"),
                                fieldWithPath("images[]").type(JsonFieldType.ARRAY).description("투표 선택지 목록"),
                                fieldWithPath("images[].id").type(JsonFieldType.NUMBER).description("투표 선택지 Id"),
                                fieldWithPath("images[].imageName").type(JsonFieldType.STRING).description("사진 이름"),
                                fieldWithPath("images[].imageUrl").type(JsonFieldType.STRING).description("사진 이미지"),
                                fieldWithPath("images[].thumbnailUrl").type(JsonFieldType.STRING).description("확대 사진 이미지"),
                                fieldWithPath("images[].voteId").type(JsonFieldType.NUMBER).optional().description("투표 Id (투표 안 한 경우 null)"),
                                fieldWithPath("shareUrl").type(JsonFieldType.STRING).description("게시글 공유 URL"),
                                fieldWithPath("createdAt").type(JsonFieldType.STRING).description("게시글 생성 시간"),
                                fieldWithPath("status").type(JsonFieldType.STRING).description("게시글 마감 여부 (PROGRESS, CLOSED)"),
                                fieldWithPath("isAuthor").type(JsonFieldType.BOOLEAN).description("게시글 작성자 여부")
                        )
                ));
    }

    @Test
    @WithMockUserInfo
    @DisplayName("게시글 삭제")
    void deletePost() throws Exception {
        //given

        //when then
        mockMvc.perform(RestDocumentationRequestBuilders.delete("/posts/{postId}", 1)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer token"))
                .andExpect(status().isOk())
                .andDo(restDocs.document(
                        requestHeaders(authorizationHeader()),
                        pathParameters(
                                parameterWithName("postId").description("게시글 Id")
                        )
                ));
        verify(postService, times(1)).delete(any(), any());
    }

    @Test
    @WithMockUserInfo
    @DisplayName("유저가 작성한 게시글 조회")
    void findMyPost() throws Exception {
        //given
        var response = new CursorBasePaginatedResponse<>(
                1L,
                false,
                List.of(
                        new SimplePostResponse(
                                1L,
                                "https://image.photopic.site/1",
                                "https://photopic.site/shareurl",
                                LocalDateTime.of(2025, 2, 13, 12, 0)
                        )
                )
        );
        given(postService.findUserPosts(1L, null, 10))
                .willReturn(response);

        //when then
        mockMvc.perform(RestDocumentationRequestBuilders.get("/posts/users/{userId}", 1)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer token"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(response)))
                .andDo(restDocs.document(
                        pathParameters(parameterWithName("userId").description("유저 Id")),
                        requestHeaders(authorizationHeader()),
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
                                        .description("게시글 데이터"),
                                fieldWithPath("data[].id")
                                        .type(JsonFieldType.NUMBER)
                                        .description("게시글 Id"),
                                fieldWithPath("data[].bestPickedImageUrl")
                                        .type(JsonFieldType.STRING)
                                        .description("가장 많은 득표를 받은 이미지 URL"),
                                fieldWithPath("data[].shareUrl")
                                        .type(JsonFieldType.STRING)
                                        .description("게시글 공유 URL"),
                                fieldWithPath("data[].createdAt")
                                        .type(JsonFieldType.STRING)
                                        .description("게시글 생성 시간")
                        )
                ));
    }

    @Test
    @WithMockUserInfo
    @DisplayName("유저가 참여한 게시글 조회")
    void findVotedPost() throws Exception {
        //given
        var response = new CursorBasePaginatedResponse<>(
                1L,
                false,
                List.of(
                        new SimplePostResponse(
                                1L,
                                "https://image.photopic.site/1",
                                "https://photopic.site/shareurl",
                                LocalDateTime.of(2025, 2, 13, 12, 0)
                        )
                )
        );
        given(postService.findVotedPosts(1L, null, 10))
                .willReturn(response);

        //when then
        mockMvc.perform(RestDocumentationRequestBuilders.get("/posts/users/{userId}/voted", 1)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer token"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(response)))
                .andDo(restDocs.document(
                        pathParameters(parameterWithName("userId").description("유저 Id")),
                        requestHeaders(authorizationHeader()),
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
                                        .description("게시글 데이터"),
                                fieldWithPath("data[].id")
                                        .type(JsonFieldType.NUMBER)
                                        .description("게시글 Id"),
                                fieldWithPath("data[].bestPickedImageUrl")
                                        .type(JsonFieldType.STRING)
                                        .description("가장 많은 득표를 받은 이미지 URL"),
                                fieldWithPath("data[].shareUrl")
                                        .type(JsonFieldType.STRING)
                                        .description("게시글 공유 URL"),
                                fieldWithPath("data[].createdAt")
                                        .type(JsonFieldType.STRING)
                                        .description("게시글 생성 시간")
                        )
                ));
    }

    @Test
    @WithMockUserInfo
    @DisplayName("게시글 공개 범위 변경")
    void toggleStatusPost() throws Exception {
        //given
        Long postId = 1L;
        doNothing().when(postService).toggleScope(any(), eq(postId));

        //when then
        mockMvc.perform(post("/posts/{postId}/scope", 1)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer token"))
                .andExpect(status().isOk())
                .andDo(restDocs.document(
                        requestHeaders(authorizationHeader()),
                        pathParameters(
                                parameterWithName("postId").description("게시글 Id")
                        )
                ));

        verify(postService, times(1)).toggleScope(any(), eq(postId));
    }

    @Test
    @WithMockUserInfo
    @DisplayName("게시글 수정")
    void updatePost() throws Exception {
        //given
        UpdatePostRequest request = new UpdatePostRequest("설명");

        //when then
        mockMvc.perform(post("/posts/{postId}/update", 1)
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
                                fieldWithPath("description")
                                        .type(JsonFieldType.STRING)
                                        .description("설명")
                                        .attributes(constraints("0~100자 사이"))
                                )
                ));
    }

    @Test
    @WithMockUserInfo
    @DisplayName("게시글 마감")
    void closePost() throws Exception {
        //given

        //when then
        mockMvc.perform(RestDocumentationRequestBuilders.post("/posts/{postId}/close", 1)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer token"))
                .andExpect(status().isOk())
                .andDo(restDocs.document(
                        requestHeaders(authorizationHeader()),
                        pathParameters(
                                parameterWithName("postId").description("게시글 Id")
                        )
                ));
        verify(postService, times(1)).close(any(), any());
    }

    @Test
    @WithMockUserInfo
    @DisplayName("피드 조회")
    void findFeed() throws Exception {
        //given
        var response = new CursorBasePaginatedResponse<> (
                1L,
                false,
                List.of(
                        new FeedResponse(
                                1L,
                                new AuthorDto(
                                        1L,
                                        "author",
                                        "https://image.photopic.site/profile-image"
                                ),
                                List.of(
                                        new PostImageResponse(1L, "뽀또A", "https://image.photopic.site/image/1", "https://image.photopic.site/image/resize/1", 1L),
                                        new PostImageResponse(2L, "뽀또B", "https://image.photopic.site/image/2", "https://image.photopic.site/image/resize/2", null)
                                ),
                                Status.PROGRESS,
                                "description",
                                "anioefw78f329jcs9",
                                true,
                                1,
                                2
                        )
                )
        );
        given(postService.findFeed(1L, null, 10)).willReturn(response);

        //when then
        mockMvc.perform(RestDocumentationRequestBuilders.get("/posts/feed")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer token"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(response)))
                .andDo(restDocs.document(
                        requestHeaders(authorizationHeader()),
                        queryParameters(cursorQueryParams()),
                        responseFields(
                                fieldWithPath("nextCursor").type(JsonFieldType.NUMBER).optional().description("다음 조회 커서 값"),
                                fieldWithPath("hasNext").type(JsonFieldType.BOOLEAN).description("다음 페이지 존재 여부 (기본 값 10)"),
                                fieldWithPath("data[]").type(JsonFieldType.ARRAY).description("게시글 데이터"),
                                fieldWithPath("data[].id").type(JsonFieldType.NUMBER).description("게시글 Id"),
                                fieldWithPath("data[].author").type(JsonFieldType.OBJECT).description("게시글 작성자 정보"),
                                fieldWithPath("data[].author.id").type(JsonFieldType.NUMBER).description("게시글 작성자 유저 ID"),
                                fieldWithPath("data[].author.nickname").type(JsonFieldType.STRING).description("게시글 작성자 닉네임"),
                                fieldWithPath("data[].author.profileUrl").type(JsonFieldType.STRING).description("게시글 작성자 프로필 이미지"),
                                fieldWithPath("data[].images[]").type(JsonFieldType.ARRAY).description("투표 선택지 목록"),
                                fieldWithPath("data[].images[].id").type(JsonFieldType.NUMBER).description("투표 선택지 Id"),
                                fieldWithPath("data[].images[].imageName").type(JsonFieldType.STRING).description("사진 이름"),
                                fieldWithPath("data[].images[].imageUrl").type(JsonFieldType.STRING).description("사진 이미지"),
                                fieldWithPath("data[].images[].thumbnailUrl").type(JsonFieldType.STRING).description("나중에 없어질 예정"),
                                fieldWithPath("data[].images[].voteId").type(JsonFieldType.NUMBER).optional().description("투표 Id (투표 안 한 경우 null)"),
                                fieldWithPath("data[].status").type(JsonFieldType.STRING).description("게시글 마감 여부 (PROGRESS, CLOSED)"),
                                fieldWithPath("data[].description").type(JsonFieldType.STRING).description("설명"),
                                fieldWithPath("data[].shareUrl").type(JsonFieldType.STRING).description("게시글 공유 URL"),
                                fieldWithPath("data[].isAuthor").type(JsonFieldType.BOOLEAN).description("게시글 작성자 여부"),
                                fieldWithPath("data[].participantCount").type(JsonFieldType.NUMBER).description("투표 참여자 수"),
                                fieldWithPath("data[].commentCount").type(JsonFieldType.NUMBER).description("투표 댓글 수")
                        )
                ));
    }
}
