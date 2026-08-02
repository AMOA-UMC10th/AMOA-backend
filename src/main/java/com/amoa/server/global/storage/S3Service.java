package com.amoa.server.global.storage;

import com.amoa.server.domain.user.exception.UserException;
import com.amoa.server.domain.user.exception.code.UserErrorCode;
import java.io.IOException;
import java.util.Set;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.exception.SdkException;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

@Slf4j
@Service
@RequiredArgsConstructor
public class S3Service {

    private static final Set<String> ALLOWED_EXTENSIONS =
            Set.of("jpg", "jpeg", "png", "webp");

    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024;

    private final S3Client s3Client;

    @Value("${aws.s3.bucket}")
    private String bucket;

    @Value("${aws.region}")
    private String region;

    public String uploadProfileImage(MultipartFile file) {
        validateFile(file);

        String extension = getExtension(file.getOriginalFilename());
        String fileName =
                "profile/" + UUID.randomUUID() + "." + extension;

        PutObjectRequest request = PutObjectRequest.builder()
                .bucket(bucket)
                .key(fileName)
                .contentType(file.getContentType())
                .build();

        try {
            s3Client.putObject(
                    request,
                    RequestBody.fromBytes(file.getBytes())
            );
        }

        catch (IOException | SdkException e) {
            log.error("S3 프로필 이미지 업로드 실패", e);

            throw new UserException(
                    UserErrorCode.PROFILE_IMAGE_UPLOAD_FAILED
            );
        }

        return createImageUrl(fileName);
    }

    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new UserException(
                    UserErrorCode.PROFILE_IMAGE_REQUIRED
            );
        }

        if (file.getSize() > MAX_FILE_SIZE) {
            throw new UserException(
                    UserErrorCode.PROFILE_IMAGE_TOO_LARGE
            );
        }

        String extension = getExtension(file.getOriginalFilename());

        if (!ALLOWED_EXTENSIONS.contains(extension)) {
            throw new UserException(
                    UserErrorCode.PROFILE_IMAGE_INVALID_FORMAT
            );
        }

        String contentType = file.getContentType();

        if (contentType == null
                || !contentType.startsWith("image/")) {
            throw new UserException(
                    UserErrorCode.PROFILE_IMAGE_INVALID_FORMAT
            );
        }
    }

    private String getExtension(String originalFilename) {
        if (originalFilename == null
                || !originalFilename.contains(".")) {
            throw new UserException(
                    UserErrorCode.PROFILE_IMAGE_INVALID_FORMAT
            );
        }

        return originalFilename
                .substring(originalFilename.lastIndexOf(".") + 1)
                .toLowerCase();
    }



    private String createImageUrl(String fileName) {
        return "https://"
                + bucket
                + ".s3."
                + region
                + ".amazonaws.com/"
                + fileName;
    }

    public void deleteProfileImage(String imageUrl) {
        if (imageUrl == null || imageUrl.isBlank()) {
            return;
        }

        String key = extractProfileImageKey(imageUrl);

        if (key == null) {
            return;
        }

        try {
            s3Client.deleteObject(builder -> builder
                    .bucket(bucket)
                    .key(key)
            );
        } catch (SdkException e) {
            log.error("S3 기존 프로필 이미지 삭제 실패. key={}", key, e);
        }
    }

    private String extractProfileImageKey(String imageUrl) {
        String expectedPrefix =
                "https://" + bucket + ".s3." + region + ".amazonaws.com/";

        if (!imageUrl.startsWith(expectedPrefix)) {
            return null;
        }

        String key = imageUrl.substring(expectedPrefix.length());

        if (!key.startsWith("profile/")) {
            return null;
        }

        return key;
    }
}