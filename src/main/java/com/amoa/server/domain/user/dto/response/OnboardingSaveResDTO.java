package com.amoa.server.domain.user.dto.response;

import lombok.Builder;

@Builder
public record OnboardingSaveResDTO(
        Long userId,
        boolean onboardingCompleted
) {}