package com.swyp8team2.image.application;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.swyp8team2.common.exception.ErrorCode;
import com.swyp8team2.common.exception.InternalServerException;
import com.swyp8team2.common.exception.ServiceUnavailableException;
import com.swyp8team2.common.util.DateTime;
import com.swyp8team2.image.presentation.dto.ImageFileDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.lambda.LambdaClient;
import software.amazon.awssdk.services.lambda.model.InvokeRequest;
import software.amazon.awssdk.services.lambda.model.InvokeResponse;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import javax.imageio.ImageIO;
import javax.imageio.IIOImage;
import javax.imageio.IIOException;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.FileImageOutputStream;
import java.awt.Graphics2D;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Base64;

@Component
@Slf4j
@RequiredArgsConstructor
public class R2Storage {

    private static final String CONVERT_EXTENSION = ".jpeg";

    @Value("${file.endpoint}")
    private String imageDomainUrl;

    @Value("${r2.bucket.name}")
    private String bucketName;

    @Value("${r2.bucket.path}")
    private String filePath;

    @Value("${aws.lambda-arn}")
    private String lambdaFunctionName;

    private final S3Client s3Client;
    private final LambdaClient lambdaClient;

    public List<ImageFileDto> uploadImageFile(MultipartFile... files) {
        List<ImageFileDto> imageFiles = new ArrayList<>();
        try {
            for (int i = 0; i < files.length; i++) {
                MultipartFile file = files[i];
                String originFilename = file.getOriginalFilename();
                String fileExtension = originFilename.substring(originFilename.lastIndexOf("."));
                Map<String, String> metadata = new HashMap<>();
                String realFileName = getRealFileName(filePath, i, CONVERT_EXTENSION);

                File tempFile = File.createTempFile("upload_", originFilename);
                file.transferTo(tempFile);
                switch(fileExtension) {
                    case ".heic", ".heif" -> {
                        convertHeicToJpg(tempFile, originFilename, realFileName);
                    } case ".png", ".gif" -> {
                        metadata.put("Content-Type", "image/jpeg");
                        s3PutObject(convertToJpg(tempFile), realFileName, metadata);
                    } default -> {
                        realFileName = getRealFileName(filePath, i, fileExtension);
                        s3PutObject(tempFile, realFileName, metadata);
                    }
                }

                String imageUrl = imageDomainUrl + realFileName;
                imageFiles.add(new ImageFileDto(originFilename, imageUrl, imageUrl));
                deleteTempFile(tempFile);
            }

            return imageFiles;
        } catch (IOException e) {
            log.error("Failed to upload file", e);
            throw new ServiceUnavailableException(ErrorCode.SERVICE_UNAVAILABLE);
        }
    }

    private void convertHeicToJpg(File sourceFile, String originFilename, String realFileName) throws IOException {
        byte[] fileContent = Files.readAllBytes(sourceFile.toPath());
        String base64Content = Base64.getEncoder().encodeToString(fileContent);

        Map<String, String> payload = new HashMap<>();
        payload.put("fileContent", base64Content);
        payload.put("originFilename", originFilename);
        payload.put("key", realFileName);

        ObjectMapper objectMapper = new ObjectMapper();
        String payloadJson = objectMapper.writeValueAsString(payload);
        InvokeRequest invokeRequest = InvokeRequest.builder()
                .functionName(lambdaFunctionName)
                .payload(SdkBytes.fromUtf8String(payloadJson))
                .build();

        InvokeResponse response = lambdaClient.invoke(invokeRequest);
        String responseJson = response.payload().asUtf8String();
        Map<String, Object> responseMap = objectMapper.readValue(responseJson, Map.class);

        if (responseMap.containsKey("errorMessage")) {
            log.error("Lambda service error, {}", responseMap.get("errorMessage"));
            throw new InternalServerException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    private File convertToJpg(File sourceFile) throws IOException {
        BufferedImage image = ImageIO.read(sourceFile);
        File jpgFile = File.createTempFile("converted_", ".jpeg");

        ImageWriter writer = ImageIO.getImageWritersByFormatName("jpeg").next();
        try (FileImageOutputStream output = new FileImageOutputStream(jpgFile)) {
            writer.setOutput(output);
            ImageWriteParam param = writer.getDefaultWriteParam();
            param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
            param.setCompressionQuality(0.9f);

            writer.write(null, new IIOImage(image, null, null), param);
            writer.dispose();
            return jpgFile;
        } catch (IIOException e) {
            log.error("Failed to convert image to jpg", e);

            // 알파 채널 처리를 위해 새 RGB 이미지 생성 (알파 채널 제거)
            BufferedImage rgbImage = new BufferedImage(
                    image.getWidth(),
                    image.getHeight(),
                    BufferedImage.TYPE_INT_RGB
            );

            // 원본 이미지를 새 RGB 이미지에 그림
            // 흰색 배경 설정
            Graphics2D graphics = rgbImage.createGraphics();
            graphics.setColor(Color.WHITE);
            graphics.fillRect(0, 0, rgbImage.getWidth(), rgbImage.getHeight());
            graphics.drawImage(image, 0, 0, null);
            graphics.dispose();

            try {
                ImageIO.write(rgbImage, "jpeg", jpgFile);
                return jpgFile;
            } catch (IOException io) {
                log.error("Error in JPG conversion: {}", io.getMessage());
                throw io;
            }
        }
    }

    private void s3PutObject(File file, String realFileName, Map<String, String> metadata) {
        PutObjectRequest objectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .metadata(metadata)
                .key(realFileName)
                .build();

        s3Client.putObject(objectRequest, RequestBody.fromFile(file));
    }

    private String getRealFileName(String filePath, int sequence, String extension) {
        return filePath + DateTime.getCurrentTimestamp() + sequence + extension;
    }

    private void deleteTempFile(File tempFile) {
        if (!tempFile.delete()) {
            log.error("Failed to delete temp file: {}", tempFile.getName());
        }
    }
}
