package com.swyp8team2.image.application;

import com.swyp8team2.common.exception.BadRequestException;
import com.swyp8team2.common.exception.ErrorCode;
import com.swyp8team2.image.domain.ImageFile;
import com.swyp8team2.image.domain.ImageFileRepository;
import com.swyp8team2.image.presentation.dto.ImageFileDto;
import com.swyp8team2.image.presentation.dto.ImageFileResponse;
import com.swyp8team2.image.util.FileValidator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ImageServiceTest {

    @Mock
    private R2Storage r2Storage;

    @Mock
    private FileValidator fileValidator;

    @Mock
    private ImageFileRepository imageFileRepository;

    @InjectMocks
    private ImageService imageService;

    @Test
    @DisplayName("ImageFile Entity 생성")
    void createImageFile() {
        // given
        ImageFileDto dto = new ImageFileDto("test.jpg", "https://image.photopic.site/test.jpg", "https://image.photopic.site/thumb.jpg");
        ImageFile imageFile = ImageFile.create(dto);

        // when
        ReflectionTestUtils.setField(imageFile, "id", 100L);
        when(imageFileRepository.save(any(ImageFile.class))).thenReturn(imageFile);
        Long id = imageService.createImageFile(dto);

        // then
        assertEquals(100L, id);
    }

    @Test
    @DisplayName("ImageFile Entity 생성 - 파라미터가 null인 경우")
    void createImageFile_null() {
        // given
        ImageFileDto dto = new ImageFileDto("test.jpg", null, null);

        // when
        when(imageFileRepository.save(any(ImageFile.class)))
                .thenThrow(new BadRequestException(ErrorCode.INVALID_ARGUMENT));

        // then
        assertThatThrownBy(() -> imageService.createImageFile(dto))
                .isInstanceOf(BadRequestException.class)
                .hasMessage(ErrorCode.INVALID_ARGUMENT.getMessage());

    }

    @Test
    @DisplayName("ImageFile Entity 생성 - 파라미터가 빈 값인 경우")
    void createImageFile_emptyString() {
        // given
        ImageFileDto dto = new ImageFileDto("test.jpg", "", "");

        // when
        when(imageFileRepository.save(any(ImageFile.class)))
                .thenThrow(new BadRequestException(ErrorCode.INVALID_ARGUMENT));

        // then
        assertThatThrownBy(() -> imageService.createImageFile(dto))
                .isInstanceOf(BadRequestException.class)
                .hasMessage(ErrorCode.INVALID_ARGUMENT.getMessage());

    }

    @Test
    @DisplayName("파일 업로드")
    void uploadImageFile() {
        // given
        MockMultipartFile file1 = new MockMultipartFile(
                "files",
                "test1.jpg",
                MediaType.IMAGE_JPEG_VALUE,
                "dummy content".getBytes(StandardCharsets.UTF_8)
        );
        MockMultipartFile file2 = new MockMultipartFile(
                "files",
                "test2.png",
                MediaType.IMAGE_PNG_VALUE,
                "dummy content".getBytes(StandardCharsets.UTF_8)
        );

        List<ImageFileDto> imageFiles = List.of(
                new ImageFileDto("test1.jpg", "https://image.photopic.site/test1.jpg", "https://image.photopic.site/thumb1.jpg"),
                new ImageFileDto("test2.png", "https://image.photopic.site/test2.png", "https://image.photopic.site/thumb2.png")
        );

        doNothing().when(fileValidator).validate(file1, file2);
        when(r2Storage.uploadImageFile(file1, file2)).thenReturn(imageFiles);

        AtomicLong idGenerator = new AtomicLong(1L);
        when(imageFileRepository.save(any(ImageFile.class))).thenAnswer(invocation -> new ImageFile() {
            @Override
            public Long getId() {
                return idGenerator.getAndIncrement();
            }
        });

        // when
        ImageFileResponse response = imageService.uploadImageFile(file1, file2);

        // then
        assertAll(
                () -> assertNotNull(response),
                () -> assertEquals(2, response.imageFileId().size()),
                () -> assertThat(response.imageFileId().get(0)).isEqualTo(1L),
                () -> assertThat(response.imageFileId().get(1)).isEqualTo(2L)
        );
    }

    @Test
    @DisplayName("파일 업로드 - IOException 발생")
    void uploadImageFile_IOException() {
        // given
        MockMultipartFile file1 = new MockMultipartFile(
                "files",
                "test1.jpg",
                MediaType.IMAGE_JPEG_VALUE,
                "dummy content".getBytes(StandardCharsets.UTF_8)
        );
        MockMultipartFile file2 = new MockMultipartFile(
                "files",
                "test2.png",
                MediaType.IMAGE_PNG_VALUE,
                "dummy content".getBytes(StandardCharsets.UTF_8)
        );

        doNothing().when(fileValidator).validate(file1, file2);
        when(r2Storage.uploadImageFile(file1, file2))
                .thenThrow(new UncheckedIOException(new IOException(ErrorCode.SERVICE_UNAVAILABLE.getMessage())));

        // when then
        assertThatThrownBy(() -> imageService.uploadImageFile(file1, file2))
                .isInstanceOf(UncheckedIOException.class)
                .hasMessageContaining(ErrorCode.SERVICE_UNAVAILABLE.getMessage());
    }
}