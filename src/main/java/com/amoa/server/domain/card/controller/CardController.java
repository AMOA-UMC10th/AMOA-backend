package com.amoa.server.domain.card.controller;

import com.amoa.server.domain.card.controller.docs.CardControllerDocs;
import com.amoa.server.domain.card.dto.request.CardReqDTO.CardSearchRequest;
import com.amoa.server.domain.card.dto.response.CardResDTO;
import com.amoa.server.domain.card.dto.response.CardResDTO.KakaoChannel;
import com.amoa.server.domain.card.exception.code.CardSuccessCode;
import com.amoa.server.domain.card.service.query.CardQueryService;
import com.amoa.server.global.apiPayload.ApiResponse;
import com.amoa.server.global.auth.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/cards")
@RequiredArgsConstructor
public class CardController implements CardControllerDocs {

    private final CardQueryService cardQueryService;

    // 카카오로 시작하기
    @GetMapping("/{cardId}/kakao-channel")
    @Override
    public ApiResponse<KakaoChannel> getKakaoChannel(
            @PathVariable Long cardId
    ) {
        return ApiResponse.onSuccess(
                CardSuccessCode.CARD_KAKAO_CHANNEL_FOUND,
                cardQueryService.getKakaoChannel(cardId)
        );
    }

    // 아트 목록 조회
    @GetMapping
    @Override
    public ApiResponse<CardResDTO.CardList> searchCards(
            @ModelAttribute CardSearchRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {

        Long userId = userDetails == null ? null : userDetails.user().getId();

        return ApiResponse.onSuccess(
                CardSuccessCode.CARD_FOUND,
                cardQueryService.searchCards(request, userId)
        );
    }

    // 아트 상세 조회
    @GetMapping("/{cardId}")
    public ApiResponse<CardResDTO.CardDetailResponse> getCardDetail(
            @PathVariable Long cardId
    ) {
        return ApiResponse.onSuccess(
                CardSuccessCode.CARD_FOUND,
                cardQueryService.getCardDetail(cardId)
        );
    }
}
