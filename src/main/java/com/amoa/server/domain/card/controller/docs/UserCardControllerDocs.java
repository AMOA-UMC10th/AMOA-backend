package com.amoa.server.domain.card.controller.docs;

import com.amoa.server.domain.card.dto.response.UserCardResDTO;
import com.amoa.server.global.apiPayload.ApiResponse;
import com.amoa.server.global.auth.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;

public interface UserCardControllerDocs {

    @Operation(
            summary = "아트카드 찜 등록",
            description = "사용자가 특정 아트카드를 찜합니다."
    )
    ApiResponse<UserCardResDTO.LikeResultDTO> createCardLike(
            @Parameter(description = "카드 ID")
            Long cardId,
            @Parameter(hidden = true)
            CustomUserDetails customUserDetails
    );


    @Operation(
            summary = "아트카드 찜 취소",
            description = "사용자가 찜한 아트카드를 취소합니다."
    )
    ApiResponse<Void> deleteCardLike(
            @Parameter(description = "카드 ID")
            Long cardId,
            @Parameter(hidden = true)
            CustomUserDetails customUserDetails
    );
}