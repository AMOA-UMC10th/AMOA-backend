package com.amoa.server.domain.shop.service.command;

import com.amoa.server.domain.shop.entity.SavedShop;
import com.amoa.server.domain.shop.dto.response.SavedShopResDTO;
import com.amoa.server.domain.shop.entity.Shop;
import com.amoa.server.domain.shop.exception.code.ShopErrorCode;
import com.amoa.server.domain.shop.repository.SavedShopRepository;
import com.amoa.server.domain.user.entity.User;
import com.amoa.server.global.apiPayload.exception.GeneralException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SavedShopService {

    private final SavedShopRepository savedShopRepository;

    @Transactional
    public SavedShopResDTO.LikeResultDTO createShopLike(User user, Shop shop){

        if(savedShopRepository.existsByUserAndShop(user, shop)){
            throw new GeneralException(ShopErrorCode.SHOP_ALREADY_LIKED);
        }

        SavedShop savedShop = new SavedShop(user, shop);
        savedShopRepository.save(savedShop);

        return SavedShopResDTO.LikeResultDTO.builder()
                .userShopId(savedShop.getUserShopId())
                .shopId(shop.getId())
                .createdAt(savedShop.getCreatedAt())
                .build();


    }

    @Transactional
    public void deleteShopLike(User user, Shop shop){

        SavedShop savedShop = savedShopRepository.findByUserAndShop(user, shop)
                .orElseThrow(() -> new GeneralException(ShopErrorCode.SHOP_LIKE_NOT_FOUND));

        savedShopRepository.delete(savedShop);

        //Check ERD first
        //shop.decreaseLikeCount();
    }
}
