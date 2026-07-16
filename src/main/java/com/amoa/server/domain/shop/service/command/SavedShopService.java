package com.amoa.server.domain.shop.service.command;

import com.amoa.server.domain.shop.dto.Response.SavedShopResDTO;
import com.amoa.server.domain.shop.entity.SavedShop;
import com.amoa.server.domain.shop.entity.Shop;
import com.amoa.server.domain.shop.exception.code.ShopErrorCode;
import com.amoa.server.domain.shop.repository.SavedShopRepository;
import com.amoa.server.domain.shop.repository.ShopRepository;
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
    private final ShopRepository shopRepository;

    @Transactional
    public SavedShopResDTO.LikeResultDTO createShopLike(User user, Long shopId){

        Shop shop = shopRepository.findById(shopId)
                .orElseThrow(() -> new GeneralException(ShopErrorCode.SHOP_LIKE_NOT_FOUND));

        if(savedShopRepository.existsByUserAndShop(user, shop)){
            throw new GeneralException(ShopErrorCode.SHOP_ALREADY_LIKED);
        }

        SavedShop savedShop = new SavedShop(user, shop);
        savedShopRepository.save(savedShop);

        return SavedShopResDTO.LikeResultDTO.builder()
                .userShopId(savedShop.getUserShopId())
                .shopId(shop.getShopId())
                .createdAt(savedShop.getCreatedAt())
                .build();


    }

    @Transactional
    public void deleteShopLike(User user, Long shopId){

        Shop shop = shopRepository.findById(shopId)
                .orElseThrow(() -> new GeneralException(ShopErrorCode.SHOP_NOT_FOUND));

        SavedShop savedShop = savedShopRepository.findByUserAndShop(user, shop)
                .orElseThrow(() -> new GeneralException(ShopErrorCode.SHOP_LIKE_NOT_FOUND));

        savedShopRepository.delete(savedShop);

        //Check ERD first
        //shop.decreaseLikeCount();
    }
}
