package com.AMOA.server.domain.card.controller.docs;

import com.AMOA.server.domain.card.dto.request.CardReqDTO;
import com.AMOA.server.domain.card.dto.response.CardResDTO.CreateCard;
import com.AMOA.server.global.apiPayload.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "Card", description = "카드 관련 API")
public interface CardControllerDocs {
    @Operation(
            summary = "아트 등록",
            description = "새로운 아트를 등록합니다. "
    )
    ApiResponse<CreateCard> createCard(@RequestBody CardReqDTO.createCard request);
}
