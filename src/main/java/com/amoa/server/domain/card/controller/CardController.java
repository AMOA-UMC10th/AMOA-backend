package com.amoa.server.domain.card.controller;

import com.amoa.server.domain.card.controller.docs.CardControllerDocs;
import com.amoa.server.domain.card.dto.response.CardResDTO.KakaoChannel;
import com.amoa.server.domain.card.exception.code.CardSuccessCode;
import com.amoa.server.domain.card.service.query.CardQueryService;
import com.amoa.server.global.apiPayload.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/cards")
@RequiredArgsConstructor
public class CardController implements CardControllerDocs {

    private final CardQueryService cardQueryService;

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
}
