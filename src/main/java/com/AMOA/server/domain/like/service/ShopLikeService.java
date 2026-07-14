package com.AMOA.server.domain.like.service;

import com.AMOA.server.domain.like.domain.SavedShop;
import com.AMOA.server.domain.shop.domain.Shop;
import com.AMOA.server.domain.like.repository.SavedShopRepository;
import com.AMOA.server.domain.user.domain.User;
import com.AMOA.server.global.apiPayload.code.LikeErrorCode;
import com.AMOA.server.global.apiPayload.exception.GeneralException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ShopLikeService {

    private final SavedShopRepository savedShopRepository;


    @Transactional
    public void createShopLike(User user, Shop shop){

        if(savedShopRepository.existsByUserAndShop(user, shop)){
            throw new GeneralException(LikeErrorCode.SHOP_ALREADY_LIKED);
        }

        SavedShop savedShop = new SavedShop(user, shop);
        savedShopRepository.save(savedShop);

        //Check ERD first
        // shop.increaseLikeCount();

    }

    @Transactional
    public void deleteShopLike(User user, Shop shop){

        SavedShop savedShop = savedShopRepository.findByUserAndShop(user, shop)
                .orElseThrow(() -> new GeneralException(LikeErrorCode.SHOP_LIKE_NOT_FOUND));

        savedShopRepository.delete(savedShop);

        //Check ERD first
        //shop.decreaseLikeCount();
    }
}
