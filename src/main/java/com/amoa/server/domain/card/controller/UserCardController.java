package com.amoa.server.domain.card.controller;

import com.amoa.server.domain.card.controller.docs.UserCardControllerDocs;
import com.amoa.server.domain.card.dto.response.UserCardResDTO;
import com.amoa.server.domain.card.exception.code.CardSuccessCode;
import com.amoa.server.domain.card.service.command.UserCardCommandService;
import com.amoa.server.domain.user.entity.User;
import com.amoa.server.global.apiPayload.ApiResponse;
import com.amoa.server.global.auth.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/cards")
public class UserCardController implements UserCardControllerDocs {

    private final UserCardCommandService userCardCommandService;

    @Override
    @PostMapping("/{cardId}/like")
    public ApiResponse<UserCardResDTO.LikeResultDTO> createCardLike(
            @PathVariable Long cardId,
            @AuthenticationPrincipal CustomUserDetails customUserDetails
            ) {

        User user = customUserDetails.user();

        UserCardResDTO.LikeResultDTO result = userCardCommandService.createCardLike(user, cardId);

        return ApiResponse.onSuccess(CardSuccessCode.CARD_LIKED, result);
    }

    @Override
    @DeleteMapping("/{cardId}/like")
    public ApiResponse<Void> deleteCardLike(
            @PathVariable Long cardId,
            @AuthenticationPrincipal CustomUserDetails customUserDetails
    ) {

        User user = customUserDetails.user();

        userCardCommandService.deleteCardLike(user, cardId);

        return ApiResponse.onSuccess(CardSuccessCode.CARD_UNLIKED, null);
    }
}