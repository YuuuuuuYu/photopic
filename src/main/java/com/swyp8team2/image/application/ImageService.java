package com.swyp8team2.image.application;

import com.swyp8team2.image.domain.ImageFile;
import com.swyp8team2.image.domain.ImageFileRepository;
import com.swyp8team2.image.presentation.dto.ImageFileDto;
import com.swyp8team2.image.presentation.dto.ImageFileResponse;
import com.swyp8team2.image.util.FileValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class ImageService {

    private final R2Storage r2Storage;
    private final FileValidator fileValidator;
    private final ImageFileRepository imageFileRepository;

    public ImageFileResponse uploadImageFile(MultipartFile... files) {
        fileValidator.validate(files);
        List<ImageFileDto> imageFiles = r2Storage.uploadImageFile(files);
        List<Long> imageFileIds = imageFiles.stream()
                .map(this::createImageFile)
                .collect(Collectors.toList());
        return new ImageFileResponse(imageFileIds);
    }

    public Long createImageFile(ImageFileDto imageFiledto) {
        ImageFile imageFile = imageFileRepository.save(ImageFile.create(imageFiledto));
        return imageFile.getId();
    }
}
