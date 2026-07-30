package com.amoa.server.domain.card.service.query;

import com.amoa.server.domain.card.converter.UserCardConverter;
import com.amoa.server.domain.card.dto.response.UserCardResDTO;
import com.amoa.server.domain.card.entity.UserCard;
import com.amoa.server.domain.card.repository.UserCardRepository;
import com.amoa.server.domain.common.enums.SortType;
import com.amoa.server.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserCardQueryService {

    private final UserCardRepository userCardRepository;

    public UserCardResDTO.LikedCardListResponse getLikedCards(
            User user,
            SortType sortType,
            Pageable pageable
    ) {

        Pageable latestPageable = PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                Sort.by(Sort.Direction.DESC, "createdAt")
        );

        Page<UserCard> userCards = switch(sortType) {

            case LATEST ->
                    userCardRepository.findAllByUser(user, latestPageable);

            case POPULAR ->
                    userCardRepository.findLikedCardsByUserOrderByPopular(user, pageable);

            case PRICE_ASC ->
                    userCardRepository.findLikedCardsByUserOrderByPriceAsc(user, pageable);

            case PRICE_DESC ->
                    userCardRepository.findLikedCardsByUserOrderByPriceDesc(user, pageable);

            case RECOMMENDED ->
                    userCardRepository.findLikedCardsByUserOrderByRecommended(user, pageable);
        };

        List<UserCardResDTO.LikedCardResponse> likedCards =
                userCards.getContent()
                        .stream()
                        .map(UserCardConverter::toLikedCardResponse)
                        .toList();

        return new UserCardResDTO.LikedCardListResponse(
                likedCards,
                userCards.getTotalElements(),
                userCards.hasNext()
        );
    }
}