package com.amoa.server.domain.card.controller.docs;

import com.amoa.server.domain.card.dto.response.CardResDTO;
import com.amoa.server.global.apiPayload.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.PathVariable;

@Tag(name = "Card(user)", description = "카드 관련 API(user)")
public interface CardControllerDocs {

    @Operation(
            summary = "카카오톡 채널 URL 조회",
            description = "카드에 연결된 샵의 카카오톡 채널 URL을 조회합니다."
    )
    ApiResponse<CardResDTO.KakaoChannel> getKakaoChannel(
            @Parameter(
                    name = "cardId",
                    description = "조회할 카드 ID",
                    required = true
            )
            @PathVariable Long cardId
    );
}
