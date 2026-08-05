package com.amoa.server.domain.card.controller.docs;

import com.amoa.server.domain.card.dto.request.CardReqDTO;
import com.amoa.server.domain.card.dto.response.CardResDTO.CreateCard;
import com.amoa.server.global.apiPayload.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "Card(management)", description = "카드 관련 API(management)")
public interface AdminCardControllerDocs {
    @Operation(
            summary = "아트 등록",
            description = "새로운 아트를 등록합니다. "
    )
    ApiResponse<CreateCard> createCard(@RequestBody CardReqDTO.createCard request);
}
