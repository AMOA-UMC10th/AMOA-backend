package com.amoa.server.domain.shop.service.query;

import com.amoa.server.domain.card.entity.Card;
import com.amoa.server.domain.card.repository.CardRepository;
import com.amoa.server.domain.shop.converter.SavedShopConverter;
import com.amoa.server.domain.shop.dto.response.SavedShopResDTO;
import com.amoa.server.domain.shop.entity.SavedShop;
import com.amoa.server.domain.common.enums.SortType;
import com.amoa.server.domain.shop.repository.SavedShopRepository;
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
public class SavedShopQueryService {

    private final SavedShopRepository savedShopRepository;
    private final CardRepository cardRepository;

    public SavedShopResDTO.LikedShopListResponse getLikedShops(

            User user,
            SortType sortType,
            Pageable pageable
    ) {

        Pageable latestPageable = PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                Sort.by(Sort.Direction.DESC, "createdAt")
        );

        Page<SavedShop> savedShops = switch(sortType) {

            case LATEST ->
                    savedShopRepository.findAllByUser(user, latestPageable);

            case POPULAR ->
                    savedShopRepository.findAllByUserOrderByPopular(user, pageable);

            case PRICE_ASC ->
                    savedShopRepository.findAllByUserOrderByPriceAsc(user, pageable);

            case PRICE_DESC ->
                    savedShopRepository.findAllByUserOrderByPriceDesc(user, pageable);

            case RECOMMENDED -> {
                if (hasRecommendationCondition(user)) {
                    yield savedShopRepository
                            .findLikedShopsByUserOrderByRecommended(user, latestPageable);
                }

                yield savedShopRepository
                        .findAllByUserOrderByPopular(user, pageable);
            }
        };

        List<SavedShopResDTO.LikedShopResponse> likedShops =
                savedShops.getContent().stream()
                        .map(savedShop -> {

                            // 해당 샵의 최신 등록 카드 5개 조회
                            List<Card> cards = cardRepository
                                    .findTop5ByShopAndDeletedAtIsNullOrderByCreatedAtDesc(savedShop.getShop());

                            // Card Entity -> CardPreviewDTO 변환
                            List<SavedShopResDTO.CardPreviewDTO> cardPreviews =
                                    cards.stream()
                                            .map(SavedShopConverter::toCardPreviewDTO)
                                            .toList();

                            // SavedShop Entity + 카드 목록 -> 응답 DTO 변환
                            return SavedShopConverter.toLikedShopResponse(savedShop, cardPreviews);
                        })
                        .toList();

        return new SavedShopResDTO.LikedShopListResponse(
                likedShops,
                savedShops.getTotalElements(),
                savedShops.hasNext()
                );
    }

    private boolean hasRecommendationCondition(User user) {
        return savedShopRepository.existsRecommendedShopByUser(user);
    }

}
