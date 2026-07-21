package com.amoa.server.domain.card.service.query;

import com.amoa.server.domain.card.converter.CardConverter;
import com.amoa.server.domain.card.dto.request.CardReqDTO.CardSearchRequest;
import com.amoa.server.domain.card.dto.response.CardResDTO;
import com.amoa.server.domain.card.entity.Card;
import com.amoa.server.domain.card.exception.CardException;
import com.amoa.server.domain.card.exception.code.CardErrorCode;
import com.amoa.server.domain.card.repository.CardRepository;
import com.amoa.server.domain.card.repository.UserCardRepository;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CardQueryService {

    private final CardRepository cardRepository;
    private final UserCardRepository userCardRepository;
    private final CardConverter cardConverter;

    // 카카오로 시작하기
    @Transactional(readOnly = true)
    public CardResDTO.KakaoChannel getKakaoChannel(Long cardId) {

        Card card = cardRepository.findById(cardId)
                .orElseThrow(() ->
                        new CardException(CardErrorCode.CARD_NOT_FOUND)
                );

        return new CardResDTO.KakaoChannel(
                card.getShop().getKakaoChannelUrl()
        );
    }

    // 아트 목록 조회
    @Transactional(readOnly = true)
    public CardResDTO.CardList searchCards(
            CardSearchRequest request,
            Long userId
    ) {
        // 전체 개수 조회(검색 결과가 총 몇 개인지)
        Long totalCount = cardRepository.countCards(request);
        // 현재 페이지 조회
        List<Card> cards = cardRepository.findCards(request);

        // 커서 페이지네이션
        int size = request.size() == null ? 20 : request.size();
        boolean hasNext = cards.size() > size;
        if (hasNext) {
            cards = cards.subList(0, size);
        }

        // 찜 여부 조회
        Set<Long> likedCardIds = getLikedCardIds(userId, cards);

        //Entity -> DTO
        List<CardResDTO.CardInfo> cardInfos = cards.stream()
                .map(card -> cardConverter.toCardInfo(card, likedCardIds))
                .toList();

        // 다음 커서 생성
        Long nextCursor = hasNext
                ? cards.get(cards.size() - 1).getId()
                : null;

        // 응답 DTO 생성
        return new CardResDTO.CardList(
                totalCount,
                cardInfos.size(),
                cardInfos,
                nextCursor,
                hasNext
        );
    }

    // 로그인 사용자가 찜한 카드 ID 조회
    private Set<Long> getLikedCardIds(Long userId, List<Card> cards) {

        // 비로그인 사용자, 카드가 0개 이면 찜 여부 조회하지 않음
        if (userId == null || cards.isEmpty()) {
            return Collections.emptySet();
        }

        // 현재 화면 카드 id만 추출
        List<Long> cardIds = cards.stream()
                .map(Card::getId)
                .toList();

        // UserCard 조회 후 카드 id를 Set으로 반환
        return userCardRepository.findByUserIdAndCardIdIn(userId, cardIds)
                .stream()
                .map(userCard -> userCard.getCard().getId())
                .collect(Collectors.toSet());
    }
}
