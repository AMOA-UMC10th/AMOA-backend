package com.amoa.server.domain.shop.repository;

import com.amoa.server.domain.shop.entity.SavedShop;
import com.amoa.server.domain.user.entity.User;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static com.amoa.server.domain.common.entity.QRegion.region;
import static com.amoa.server.domain.shop.entity.QSavedShop.savedShop;
import static com.amoa.server.domain.shop.entity.QShop.shop;
import static com.amoa.server.domain.user.entity.mapping.QUserRegion.userRegion;

@RequiredArgsConstructor
public class SavedShopRepositoryImpl
        implements SavedShopRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<SavedShop> findLikedShopsByUserOrderByRecommended(
            User user,
            Pageable pageable
    ) {

        List<SavedShop> content =
                queryFactory
                        .selectFrom(savedShop)
                        .join(savedShop.shop, shop).fetchJoin()
                        .join(shop.region, region).fetchJoin()
                        .where(
                                savedShop.user.eq(user)
                        )
                        .orderBy(
                                locationScore(user).desc(),
                                shop.likeShop.desc(),
                                savedShop.createdAt.desc(),
                                savedShop.id.desc()
                        )
                        .offset(pageable.getOffset())
                        .limit(pageable.getPageSize())
                        .fetch();


        Long total =
                queryFactory
                        .select(savedShop.count())
                        .from(savedShop)
                        .where(
                                savedShop.user.eq(user)
                        )
                        .fetchOne();


        return new PageImpl<>(
                content,
                pageable,
                total == null ? 0 : total
        );
    }

    private NumberExpression<Integer> locationScore(User user) {
        return new CaseBuilder()
                .when(
                        JPAExpressions
                                .selectOne()
                                .from(userRegion)
                                .where(
                                        userRegion.user.eq(user),
                                        userRegion.region.secondDepth
                                                .eq(shop.region.secondDepth),
                                        userRegion.region.thirdDepth
                                                .eq(shop.region.thirdDepth)
                                )
                                .exists()
                )
                .then(2)

                .when(
                        JPAExpressions
                                .selectOne()
                                .from(userRegion)
                                .where(
                                        userRegion.user.eq(user),
                                        userRegion.region.secondDepth
                                                .eq(shop.region.secondDepth)
                                )
                                .exists()
                )
                .then(1)

                .otherwise(0);
    }

    @Override
    public boolean existsRecommendedShopByUser(User user) {

        return queryFactory
                .selectOne()
                .from(savedShop)
                .join(savedShop.shop, shop)
                .join(shop.region, region)
                .where(
                        savedShop.user.eq(user),
                        JPAExpressions
                                .selectOne()
                                .from(userRegion)
                                .where(
                                        userRegion.user.eq(user),
                                        userRegion.region.eq(region)
                                )
                                .exists()
                )
                .fetchFirst() != null;
    }

}
