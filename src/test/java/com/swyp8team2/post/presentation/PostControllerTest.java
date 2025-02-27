package com.swyp8team2.post.presentation;

import com.swyp8team2.common.dto.CursorBasePaginatedResponse;
import com.swyp8team2.post.presentation.dto.AuthorDto;
import com.swyp8team2.post.presentation.dto.CreatePostRequest;
import com.swyp8team2.post.presentation.dto.PostImageVoteStatusResponse;
import com.swyp8team2.post.presentation.dto.PostResponse;
import com.swyp8team2.post.presentation.dto.SimplePostResponse;
import com.swyp8team2.post.presentation.dto.PostImageRequestDto;
import com.swyp8team2.post.presentation.dto.PostImageResponse;
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
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
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
                List.of(new PostImageRequestDto(1L), new PostImageRequestDto(2L))
        );

        //when then
        mockMvc.perform(post("/posts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header(HttpHeaders.AUTHORIZATION, "Bearer token"))
                .andExpect(status().isOk())
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
                                        .description("투표 후보 이미지 ID")
                        )));
    }

    @Test
    @WithAnonymousUser
    @DisplayName("게시글 상세 조회")
    void findPost() throws Exception {
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
                        new PostImageResponse(1L, "뽀또A", "https://image.photopic.site/image/1", "https://image.photopic.site/image/resize/1", true),
                        new PostImageResponse(2L, "뽀또B", "https://image.photopic.site/image/2", "https://image.photopic.site/image/resize/2", false)
                ),
                "https://photopic.site/shareurl",
                LocalDateTime.of(2025, 2, 13, 12, 0)
        );
        given(postService.findById(any(), any()))
                .willReturn(response);

        //when then
        mockMvc.perform(RestDocumentationRequestBuilders.get("/posts/{postId}", 1))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(response)))
                .andDo(restDocs.document(
                        pathParameters(
                                parameterWithName("postId").description("게시글 Id")
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
                                fieldWithPath("images[].voted").type(JsonFieldType.BOOLEAN).description("투표 여부"),
                                fieldWithPath("shareUrl").type(JsonFieldType.STRING).description("게시글 공유 URL"),
                                fieldWithPath("createdAt").type(JsonFieldType.STRING).description("게시글 생성 시간")
                        )
                ));
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
        given(postService.findPostStatus(1L))
                .willReturn(response);

        //when then
        mockMvc.perform(RestDocumentationRequestBuilders.get("/posts/{postId}/status", 1)
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
    @DisplayName("내가 작성한 게시글 조회")
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
        given(postService.findMyPosts(1L, null, 10))
                .willReturn(response);

        //when then
        mockMvc.perform(get("/posts/me")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer token"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(response)))
                .andDo(restDocs.document(
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
    @DisplayName("내가 참여한 게시글 조회")
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
        mockMvc.perform(get("/posts/voted")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer token"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(response)))
                .andDo(restDocs.document(
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
}
