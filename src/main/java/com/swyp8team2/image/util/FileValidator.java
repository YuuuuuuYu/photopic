package com.swyp8team2.image.util;

import com.swyp8team2.common.exception.BadRequestException;
import com.swyp8team2.common.exception.ErrorCode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class FileValidator {

    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024; // 10MB

    private final Set<String> allowedExtensions;

    public FileValidator(@Value("${file.allowed-extensions}") String allowedExtensionsConfig) {
        this.allowedExtensions = Arrays.stream(allowedExtensionsConfig.split(","))
                .map(String::trim)
                .map(String::toLowerCase)
                .collect(Collectors.toSet());
    }

    public void validate(MultipartFile... files) {
        Arrays.stream(files)
                .forEach(this::validate);
    }

    private void validate(MultipartFile file) {
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new BadRequestException(ErrorCode.EXCEED_MAX_FILE_SIZE);
        }

        String originalFilename = file.getOriginalFilename();
        if (originalFilename.length() > 100) {
            throw new BadRequestException(ErrorCode.FILE_NAME_TOO_LONG);
        }

        String ext = Optional.of(originalFilename)
                .filter(name -> name.contains("."))
                .map(name -> name.substring(name.lastIndexOf('.') + 1))
                .orElseThrow(() -> new BadRequestException(ErrorCode.MISSING_FILE_EXTENSION))
                .toLowerCase();

        if (!allowedExtensions.contains(ext)) {
            throw new BadRequestException(ErrorCode.UNSUPPORTED_FILE_EXTENSION);
        }
    }
}