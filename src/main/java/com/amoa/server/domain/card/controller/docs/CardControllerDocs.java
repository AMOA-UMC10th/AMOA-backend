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

    @Operation(
            summary = "아트 추천 목록 조회",
            description = """
                    카드 상세 페이지 스크롤 시 노출되는 추천 아트 목록을 조회합니다.
                    현재 보고 있는 카드와 지역·디자인 태그가 유사한 카드를 최대 6개 반환합니다.
                    비로그인 사용자도 조회 가능합니다.
                    
                    - 지역 우선순위: 1depth부터 순서대로 몇 단계까지 일치하는지로 결정
                      (1+2+3depth 일치 > 1+2depth 일치 > 1depth만 일치)
                      , 1depth조차 다르면 후보에서 제외됩니다.
                    - 디자인 태그 우선순위: 겹치는 태그 개수가 많을수록 우선
                      , 1개도 겹치지 않으면 후보에서 제외됩니다.
                    - 두 조건을 모두 만족하는 카드 중 상위 6개를 반환하며,
                      조건을 만족하는 카드가 6개 미만이면 있는 만큼만 반환합니다.
                    - 페이지네이션이 없는 단건 목록 조회입니다.
                    """
    )
    ApiResponse<CardResDTO.RecommendedCardList> getRecommendedCards(
            @Parameter(
                    name = "cardId",
                    description = "기준이 되는 카드 ID (현재 보고 있는 카드 상세)",
                    required = true
            )
            @PathVariable Long cardId,

            @Parameter(hidden = true)
            @AuthenticationPrincipal CustomUserDetails userDetails
    );

    @Operation(
            summary = "아트 상세 조회",
            description = "카드 ID를 기반으로 아트 상세 정보를 조회합니다."
    )
    ApiResponse<CardResDTO.CardDetailResponse> getCardDetail(
            @Parameter(
                    name = "cardId",
                    description = "조회할 카드 ID",
                    required = true
            )
            @PathVariable Long cardId
    );
}
