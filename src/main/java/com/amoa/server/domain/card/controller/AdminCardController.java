package com.amoa.server.domain.card.controller;

import com.amoa.server.domain.card.controller.docs.AdminCardControllerDocs;
import com.amoa.server.domain.card.dto.request.CardReqDTO;
import com.amoa.server.domain.card.dto.response.CardResDTO.CreateCard;
import com.amoa.server.domain.card.exception.code.CardSuccessCode;
import com.amoa.server.domain.card.service.command.CardCommandService;
import com.amoa.server.global.apiPayload.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/admin/cards")
@RequiredArgsConstructor
public class AdminCardController implements AdminCardControllerDocs {

    private final CardCommandService cardCommandService;

    // 아트 등록
    @PostMapping
    @Override
    public ApiResponse<CreateCard> createCard(@RequestBody CardReqDTO.createCard request) {
        CreateCard response = cardCommandService.createCard(request);
        return ApiResponse.onSuccess(CardSuccessCode.CARD_CREATED, response);
    }

}
