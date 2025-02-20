package com.swyp8team2.image.presentation;

import com.swyp8team2.image.application.ImageService;
import com.swyp8team2.image.presentation.dto.ImageFileResponse;
import com.swyp8team2.support.RestDocsTest;
import com.swyp8team2.support.WithMockUserInfo;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.partWithName;
import static org.springframework.restdocs.request.RequestDocumentation.requestParts;
import static org.springframework.restdocs.snippet.Attributes.key;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ImageController.class)
@Import(ImageControllerTest.TestConfig.class)
class ImageControllerTest extends RestDocsTest {

    @Autowired
    private ImageService imageService;

    @TestConfiguration
    static class TestConfig {
        @Bean
        public ImageService imageService() {
            return Mockito.mock(ImageService.class);
        }
    }

    @Test
    @WithMockUserInfo
    @DisplayName("이미지 업로드")
    void createImageFile() throws Exception {
        //given
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
        ImageFileResponse response = new ImageFileResponse(List.of(1L, 2L));

        // stub
        when(imageService.uploadImageFile(file1, file2)).thenReturn(response);

        //when then
        mockMvc.perform(MockMvcRequestBuilders.multipart("/image/upload")
                        .file(file1)
                        .file(file2)
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer token"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(response)))
                .andDo(restDocs.document(
                        requestHeaders(authorizationHeader()),
                        requestParts(
                                partWithName("files")
                                        .description("투표 후보 이미지 파일")
                                        .attributes(key("type").value("Array[File]"))
                                        .attributes(constraints("최소 2개"))
                        ),
                        responseFields(
                                fieldWithPath("imageFileId")
                                        .description("업로드된 이미지 파일")
                                        .type(JsonFieldType.ARRAY)
                        )
                ));

    }
}