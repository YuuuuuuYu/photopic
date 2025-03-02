package com.swyp8team2.image.application;

import com.sksamuel.scrimage.ImmutableImage;
import com.swyp8team2.common.exception.BadRequestException;
import com.swyp8team2.common.exception.ErrorCode;
import com.swyp8team2.common.exception.ServiceUnavailableException;
import com.swyp8team2.common.util.DateTime;
import com.swyp8team2.image.presentation.dto.ImageFileDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.imgscalr.Scalr;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
@Slf4j
@RequiredArgsConstructor
public class R2Storage {

    @Value("${file.endpoint}")
    private String imageDomainUrl;

    @Value("${r2.bucket.name}")
    private String bucketName;

    @Value("${r2.bucket.path}")
    private String filePath;

    @Value("${r2.bucket.resize-path}")
    private String resizedFilePath;

    @Value("${r2.bucket.resize-height}")
    private int resizeHeight;

    private final S3Client s3Client;

    public List<ImageFileDto> uploadImageFile(MultipartFile... files) {
        List<ImageFileDto> imageFiles = new ArrayList<>();
        List<File> tempFiles = new ArrayList<>();
        try {
            for (int i = 0; i < files.length; i++) {
                MultipartFile file = files[i];
                String originFileName = file.getOriginalFilename();
                if (originFileName.length() > 100) {
                    throw new BadRequestException(ErrorCode.FILE_NAME_TOO_LONG);
                }
                String realFileName = getRealFileName(originFileName, filePath, i);
                File tempFile = File.createTempFile("upload_", "_" + originFileName);
                file.transferTo(tempFile);

                int splitIndex = originFileName.lastIndexOf("/");
                String imageType = originFileName.substring(splitIndex + 1).toLowerCase();

                File originFile = s3PutObject(tempFile, realFileName, imageType);
                String imageUrl = imageDomainUrl + realFileName;
                String resizeImageUrl = resizeImage(tempFile, realFileName, resizeHeight);

                log.debug("uploadImageFile originFileName: {}, imageUrl: {}, resizeImageUrl: {}",
                        originFileName, imageUrl, resizeImageUrl);

                imageFiles.add(new ImageFileDto(originFileName, imageUrl, resizeImageUrl));
                tempFiles.add(originFile);
            }

            return imageFiles;
        } catch (IOException e) {
            log.error("Failed to create temp file", e);
            throw new ServiceUnavailableException(ErrorCode.SERVICE_UNAVAILABLE);
        } finally {
            tempFiles.forEach(this::deleteTempFile);
        }
    }

    private String resizeImage(File file, String realFileName, int targetHeight) {
        try {
            String ext = Optional.of(realFileName)
                    .filter(name -> name.contains("."))
                    .map(name -> name.substring(name.lastIndexOf('.') + 1))
                    .orElseThrow(() -> new BadRequestException(ErrorCode.MISSING_FILE_EXTENSION))
                    .toLowerCase();

            BufferedImage srcImage;
            if ("webp".equals(ext)) {
                srcImage = ImmutableImage.loader().fromFile(file).awt();
            } else {
                srcImage = ImageIO.read(file);
            }
            BufferedImage resizedImage = highQualityResize(srcImage, targetHeight);

            int splitIndex = realFileName.lastIndexOf("/") + 1;
            realFileName = realFileName.substring(splitIndex);
            String dstKey = resizedFilePath + realFileName;
            String imageType = realFileName.substring(realFileName.lastIndexOf(".") + 1).toLowerCase();

            File tempFile = File.createTempFile("resized_", "." + imageType);
            ImageIO.write(resizedImage, imageType, tempFile);

            s3PutObject(tempFile, dstKey, imageType);
            deleteTempFile(tempFile);

            return imageDomainUrl + dstKey;
        } catch (IOException e) {
            log.error("Failed to create temp file", e);
            throw new ServiceUnavailableException(ErrorCode.SERVICE_UNAVAILABLE);
        }
    }

    private BufferedImage highQualityResize(BufferedImage originalImage, int targetHeight) {
        int originalWidth = originalImage.getWidth();
        int originalHeight = originalImage.getHeight();

        double scale = (double) targetHeight / originalHeight;
        int targetWidth = (int) (originalWidth * scale);

        return Scalr.resize(originalImage, Scalr.Method.QUALITY, Scalr.Mode.FIT_EXACT, targetWidth, targetHeight);
    }

    private File s3PutObject(File file, String realFileName, String imageType) {
        Map<String, String> metadata = getMetadataMap(imageType);
        PutObjectRequest objectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .metadata(metadata)
                .key(realFileName)
                .build();

        PutObjectResponse putObjectResponse = s3Client.putObject(objectRequest, RequestBody.fromFile(file));
        return file;
    }

    private Map<String, String> getMetadataMap(String imageType) {
        Map<String, String> metadata = new HashMap<>();
        switch (imageType) {
            case "png" -> {
                metadata.put("Content-Type", "image/png");
            } case "gif" -> {
                metadata.put("Content-Type", "image/gif");
            } case "webp" -> {
                metadata.put("Content-Type", "image/webp");
            } default -> {
                metadata.put("Content-Type", "image/jpeg");
            }
        }
        return metadata;
    }

    private String getRealFileName(String originFileName, String filePath, int sequence) {
        String objectType = originFileName.substring(originFileName.lastIndexOf(".")).toLowerCase();
        return filePath + DateTime.getCurrentTimestamp() + sequence + objectType;
    }

    private void deleteTempFile(File tempFile) {
        if (!tempFile.delete()) {
            log.error("Failed to delete temp file: {}", tempFile.getName());
        }
    }
}
