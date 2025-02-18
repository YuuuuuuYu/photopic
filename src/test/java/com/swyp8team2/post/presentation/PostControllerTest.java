package com.swyp8team2.post.presentation;

import com.swyp8team2.common.dto.CursorBasePaginatedResponse;
import com.swyp8team2.post.presentation.dto.AuthorDto;
import com.swyp8team2.post.presentation.dto.PostResponse;
import com.swyp8team2.post.presentation.dto.SimplePostResponse;
import com.swyp8team2.post.presentation.dto.VoteResponseDto;
import com.swyp8team2.support.RestDocsTest;
import com.swyp8team2.support.WithMockUserInfo;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.restdocs.snippet.Attributes.key;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class PostControllerTest extends RestDocsTest {

    @Test
    @WithMockUserInfo
    @DisplayName("게시글 생성")
    void createPost() throws Exception {
        //given
        MockMultipartFile description = new MockMultipartFile(
                "description",
                null,
                null,
                "게시물 설명".getBytes(StandardCharsets.UTF_8)
        );
        MockMultipartFile file1 = new MockMultipartFile(
                "files",
                "image.jpg",
                MediaType.IMAGE_JPEG_VALUE,
                "".getBytes(StandardCharsets.UTF_8)
        );
        MockMultipartFile file2 = new MockMultipartFile(
                "files",
                "image.png",
                MediaType.IMAGE_PNG_VALUE,
                "".getBytes(StandardCharsets.UTF_8)
        );

        //when then
        mockMvc.perform(MockMvcRequestBuilders.multipart("/posts")
                        .file(description)
                        .file(file1)
                        .file(file2)
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer token"))
                .andExpect(status().isOk())
                .andDo(restDocs.document(
                        requestHeaders(authorizationHeader()),
                        requestParts(
                                partWithName("description")
                                        .description("설명")
                                        .attributes(key("type").value("String"))
                                        .attributes(constraints("1~200자 사이")),
                                partWithName("files")
                                        .description("투표 후보 이미지 파일")
                                        .attributes(key("type").value("Array[File]"))
                                        .attributes(constraints("최소 2개"))
                        )
                ));
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
                        "https://image.photopic.site/imagePath/profile-image"
                ),
                "description",
                List.of(
                        new VoteResponseDto(1L, "https://image.photopic.site/imagePath/1", 62.75, true),
                        new VoteResponseDto(2L, "https://image.photopic.site/imagePath/2", 37.25, false)
                ),
                "https://photopic.site/shareurl",
                LocalDateTime.of(2025, 2, 13, 12, 0)
        );

        //when then
        mockMvc.perform(RestDocumentationRequestBuilders.get("/posts/{shareUrl}", "shareUrl"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(response)))
                .andDo(restDocs.document(
                        pathParameters(
                                parameterWithName("shareUrl").description("게시글 공유 URL")
                        ),
                        responseFields(
                                fieldWithPath("id").type(JsonFieldType.NUMBER).description("게시글 Id"),
                                fieldWithPath("author").type(JsonFieldType.OBJECT).description("게시글 작성자 정보"),
                                fieldWithPath("author.id").type(JsonFieldType.NUMBER).description("게시글 작성자 유저 Id"),
                                fieldWithPath("author.nickname").type(JsonFieldType.STRING).description("게시글 작성자 닉네임"),
                                fieldWithPath("author.profileUrl").type(JsonFieldType.STRING).description("게시글 작성자 프로필 이미지"),
                                fieldWithPath("description").type(JsonFieldType.STRING).description("설명"),
                                fieldWithPath("votes[]").type(JsonFieldType.ARRAY).description("투표 선택지 목록"),
                                fieldWithPath("votes[].id").type(JsonFieldType.NUMBER).description("투표 선택지 Id"),
                                fieldWithPath("votes[].imageUrl").type(JsonFieldType.STRING).description("투표 이미지"),
                                fieldWithPath("votes[].voteRatio").type(JsonFieldType.NUMBER).description("득표 비율"),
                                fieldWithPath("votes[].voted").type(JsonFieldType.BOOLEAN).description("투표 여부"),
                                fieldWithPath("shareUrl").type(JsonFieldType.STRING).description("게시글 공유 URL"),
                                fieldWithPath("createdAt").type(JsonFieldType.STRING).description("게시글 생성 시간")
                        )
                ));
    }

    @Test
    @WithMockUserInfo
    @DisplayName("게시글 삭제")
    void deletePost() throws Exception {
        //given

        //when then
        mockMvc.perform(RestDocumentationRequestBuilders.delete("/posts/{shareUrl}", "shareUrl")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer token"))
                .andExpect(status().isOk())
                .andDo(restDocs.document(
                        requestHeaders(authorizationHeader()),
                        pathParameters(
                                parameterWithName("shareUrl").description("게시글 공유 URL")
                        )
                ));
    }

    @Test
    @WithMockUserInfo
    @DisplayName("내가 작성한 게시글 조회")
    void findMyPost() throws Exception {
        //given
        CursorBasePaginatedResponse<SimplePostResponse> response = new CursorBasePaginatedResponse<>(
                1L,
                false,
                List.of(
                        new SimplePostResponse(
                                1L,
                                "https://image.photopic.site/imagePath/1",
                                "https://photopic.site/shareurl",
                                LocalDateTime.of(2025, 2, 13, 12, 0)
                        )
                )
        );

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
        CursorBasePaginatedResponse<SimplePostResponse> response = new CursorBasePaginatedResponse<>(
                1L,
                false,
                List.of(
                        new SimplePostResponse(
                                1L,
                                "https://image.photopic.site/imagePath/1",
                                "https://photopic.site/shareurl",
                                LocalDateTime.of(2025, 2, 13, 12, 0)
                        )
                )
        );

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
}
