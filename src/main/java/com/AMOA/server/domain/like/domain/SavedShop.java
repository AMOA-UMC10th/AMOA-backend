package com.AMOA.server.domain.like.domain;

import com.AMOA.server.domain.shop.domain.Shop;
import com.AMOA.server.domain.user.domain.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Getter
@Entity
@Table(name = "saved_shop")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SavedShop {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shop_id", nullable = false)
    private Shop shop;

    public SavedShop(User user, Shop shop){
        this.user = user;
        this.shop = shop;
    }


}
