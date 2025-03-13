package com.swyp8team2.image.util;

import com.swyp8team2.common.exception.BadRequestException;
import com.swyp8team2.common.exception.ErrorCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;

import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

class FileValidatorTest {

    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024; // 10MB

    private FileValidator fileValidator;

    @BeforeEach
    void setUp() {
        String allowedExtensions = "gif,jpg,jpeg,png,webp,heic,heif";
        fileValidator = new FileValidator(allowedExtensions);
    }

    @Test
    @DisplayName("파일 유효성 체크 - 파일 크기 초과")
    void validate_exceedMaxFileSize() {
        // given
        byte[] largeContent = new byte[(int) (MAX_FILE_SIZE + 1)];
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.jpg",
                MediaType.IMAGE_JPEG_VALUE,
                largeContent
        );

        // when then
        assertThatThrownBy(() -> fileValidator.validate(file))
                .isInstanceOf(BadRequestException.class)
                .hasMessage(ErrorCode.EXCEED_MAX_FILE_SIZE.getMessage());
    }

    @Test
    @DisplayName("파일 유효성 체크 - 지원하지 않는 확장자")
    void validate_unsupportedFileExtension() {
        // given
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.txt",
                "text/plain",
                "dummy content".getBytes(StandardCharsets.UTF_8)
        );

        // when then
        assertThatThrownBy(() -> fileValidator.validate(file))
                .isInstanceOf(BadRequestException.class)
                .hasMessage(ErrorCode.UNSUPPORTED_FILE_EXTENSION.getMessage());
    }

    @Test
    @DisplayName("파일 유효성 체크 - 파일명 너무 김")
    void validate_fileNameTooLong() {
        // given
        String filename = new String(new char[101])+".jpeg";
        MockMultipartFile file = new MockMultipartFile(
                "file",
                filename,
                "",
                "dummy content".getBytes(StandardCharsets.UTF_8)
        );

        // when then
        assertThatThrownBy(() -> fileValidator.validate(file))
                .isInstanceOf(BadRequestException.class)
                .hasMessage(ErrorCode.FILE_NAME_TOO_LONG.getMessage());
    }

    @Test
    @DisplayName("파일 유효성 체크 - 확장자 누락")
    void validate_missingFileExtension() {
        // given
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test",
                "text/plain",
                "dummy content".getBytes(StandardCharsets.UTF_8)
        );

        // when then
        assertThatThrownBy(() -> fileValidator.validate(file))
                .isInstanceOf(BadRequestException.class)
                .hasMessage(ErrorCode.MISSING_FILE_EXTENSION.getMessage());
    }

    @Test
    @DisplayName("파일 유효성 체크 - 여러 파일 중 하나가 유효성 실패")
    void validate_multipleFilesOneInvalid() {
        // given
        MockMultipartFile file1 = new MockMultipartFile(
                "file",
                "test.jpg",
                "image/jpeg",
                "dummy".getBytes(StandardCharsets.UTF_8)
        );
        byte[] largeContent = new byte[(int) (MAX_FILE_SIZE + 1)];
        MockMultipartFile file2 = new MockMultipartFile(
                "file",
                "large.jpg",
                "image/jpeg",
                largeContent
        );

        // when then
        assertThatThrownBy(() -> fileValidator.validate(file1, file2))
                .isInstanceOf(BadRequestException.class)
                .hasMessage(ErrorCode.EXCEED_MAX_FILE_SIZE.getMessage());
    }
}