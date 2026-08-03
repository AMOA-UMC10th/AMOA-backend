package com.amoa.server.domain.user.service.command;

import com.amoa.server.domain.user.entity.User;
import com.amoa.server.domain.user.exception.UserException;
import com.amoa.server.domain.user.exception.code.UserErrorCode;
import com.amoa.server.domain.user.repository.UserRepository;
import com.amoa.server.global.storage.S3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@Service
@RequiredArgsConstructor
public class UserProfileImageCommandService {

    private final UserRepository userRepository;
    private final S3Service s3Service;

    @Transactional
    public void updateProfileImageUrl(
            Long userId,
            String newImageUrl
    ) {
        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new UserException(UserErrorCode.USER_NOT_FOUND)
                );

        String previousImageUrl = user.getProfileImageUrl();

        user.updateProfileImageUrl(newImageUrl);

        registerPreviousImageCleanup(previousImageUrl);
    }

    private void registerPreviousImageCleanup(
            String previousImageUrl
    ) {
        if (previousImageUrl == null
                || previousImageUrl.isBlank()) {
            return;
        }

        TransactionSynchronizationManager.registerSynchronization(
                new TransactionSynchronization() {
                    @Override
                    public void afterCommit() {
                        s3Service.deleteProfileImage(
                                previousImageUrl
                        );
                    }
                }
        );
    }
}