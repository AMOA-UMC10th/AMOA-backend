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
                .contentType(resolveContentType(extension))
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

        // 실제 파일 바이트를 보고 이미지 형식 확인
        String detectedType = detectImageType(file);

        boolean matched = switch (detectedType) {
            case "jpeg" ->
                    extension.equals("jpg")
                            || extension.equals("jpeg");

            case "png" ->
                    extension.equals("png");

            case "webp" ->
                    extension.equals("webp");

            default -> false;
        };

        if (!matched) {
            throw new UserException(
                    UserErrorCode.PROFILE_IMAGE_INVALID_FORMAT
            );
        }
    }

    private String detectImageType(MultipartFile file) {
        try {
            byte[] bytes = file.getBytes();

            if (bytes.length >= 3
                    && (bytes[0] & 0xFF) == 0xFF
                    && (bytes[1] & 0xFF) == 0xD8
                    && (bytes[2] & 0xFF) == 0xFF) {
                return "jpeg";
            }

            if (bytes.length >= 8
                    && (bytes[0] & 0xFF) == 0x89
                    && bytes[1] == 0x50
                    && bytes[2] == 0x4E
                    && bytes[3] == 0x47) {
                return "png";
            }

            if (bytes.length >= 12
                    && bytes[0] == 'R'
                    && bytes[1] == 'I'
                    && bytes[2] == 'F'
                    && bytes[3] == 'F'
                    && bytes[8] == 'W'
                    && bytes[9] == 'E'
                    && bytes[10] == 'B'
                    && bytes[11] == 'P') {
                return "webp";
            }

            throw new UserException(
                    UserErrorCode.PROFILE_IMAGE_INVALID_FORMAT
            );

        } catch (IOException e) {
            throw new UserException(
                    UserErrorCode.PROFILE_IMAGE_INVALID_FORMAT
            );
        }
    }

    private String resolveContentType(String extension) {
        return switch (extension) {
            case "jpg", "jpeg" -> "image/jpeg";
            case "png" -> "image/png";
            case "webp" -> "image/webp";
            default -> throw new UserException(
                    UserErrorCode.PROFILE_IMAGE_INVALID_FORMAT
            );
        };
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