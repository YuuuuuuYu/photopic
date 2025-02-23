package com.swyp8team2.image.presentation;

import com.swyp8team2.image.application.ImageService;
import com.swyp8team2.image.presentation.dto.ImageFileResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/image")
public class ImageController {

    private final ImageService r2Service;

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ImageFileResponse> createImageFile(@RequestPart("files") MultipartFile... files) {
        ImageFileResponse response = r2Service.uploadImageFile(files);
        return ResponseEntity.ok(response);
    }

}
