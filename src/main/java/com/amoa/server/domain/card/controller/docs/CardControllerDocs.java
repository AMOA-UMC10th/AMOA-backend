package com.amoa.server.domain.card.controller.docs;

import com.amoa.server.domain.card.dto.request.CardReqDTO.CardSearchRequest;
import com.amoa.server.domain.card.dto.response.CardResDTO;
import com.amoa.server.global.apiPayload.ApiResponse;
import com.amoa.server.global.auth.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.ModelAttribute;
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

    @Operation(
            summary = "아트 목록 조회",
            description = """
                    조건에 맞는 아트 목록을 조회합니다.
                    커서 기반 페이지네이션을 사용합니다.
                    
                    - size 기본값: 20
                    - sort:
                      LATEST 최신순
                      POPULAR 인기순
                      PRICE_ASC 가격 낮은순
                      PRICE_DESC 가격 높은순
                      RECOMMENDED 추천순
                    """
    )
    ApiResponse<CardResDTO.CardList> searchCards(
            @ParameterObject
            @ModelAttribute CardSearchRequest request,

            @Parameter(hidden = true)
            @AuthenticationPrincipal CustomUserDetails userDetails
    );
}
