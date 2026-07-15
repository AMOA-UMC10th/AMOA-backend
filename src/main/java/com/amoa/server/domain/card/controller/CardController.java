package com.amoa.server.domain.card.controller;

import com.amoa.server.domain.card.controller.docs.CardControllerDocs;
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
@RequestMapping("/api/admin/cards")
@RequiredArgsConstructor
public class CardController implements CardControllerDocs {

    private final CardCommandService cardCommandService;

    //
    @PostMapping
    @Override
    public ApiResponse<CreateCard> createCard(@RequestBody CardReqDTO.createCard request) {
        CreateCard response = cardCommandService.createCard(request);
        return ApiResponse.onSuccess(CardSuccessCode.CARD_CREATED, response);
    }
}
