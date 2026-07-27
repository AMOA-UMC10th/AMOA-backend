package com.amoa.server.domain.card.service.command;

import com.amoa.server.domain.card.dto.response.UserCardResDTO;
import com.amoa.server.domain.card.entity.Card;
import com.amoa.server.domain.card.entity.UserCard;
import com.amoa.server.domain.card.exception.code.CardErrorCode;
import com.amoa.server.domain.card.repository.CardRepository;
import com.amoa.server.domain.user.entity.User;
import com.amoa.server.global.apiPayload.exception.GeneralException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.amoa.server.domain.card.repository.UserCardRepository;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserCardCommandService {

    private final UserCardRepository userCardRepository;
    private final CardRepository cardRepository;

    @Transactional
    public UserCardResDTO.LikeResultDTO createCardLike(User user, Long cardId) {

        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> new GeneralException(CardErrorCode.CARD_NOT_FOUND));

        if (userCardRepository.existsByUserAndCard(user, card)) {
            throw new GeneralException(CardErrorCode.CARD_ALREADY_LIKED);
        }

        UserCard userCard = new UserCard(user, card);
        userCardRepository.save(userCard);

        cardRepository.increaseLikeCount(cardId);

        return new UserCardResDTO.LikeResultDTO(
                userCard.getId(),
                card.getId(),
                userCard.getCreatedAt()
                );
    }


    @Transactional
    public void deleteCardLike(User user, Long cardId) {

        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> new GeneralException(CardErrorCode.CARD_NOT_FOUND));

        UserCard userCard = userCardRepository.findByUserAndCard(user, card)
                .orElseThrow(() -> new GeneralException(CardErrorCode.CARD_LIKE_NOT_FOUND));

        userCardRepository.delete(userCard);

        cardRepository.decreaseLikeCount(cardId);
    }
}
