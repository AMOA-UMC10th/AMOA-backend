package com.amoa.server.domain.card.repository;

import com.amoa.server.domain.card.entity.UserCard;
import com.amoa.server.domain.user.entity.User;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.jpa.JPAExpressions;
import org.springframework.data.domain.PageImpl;

import java.util.List;

import static com.amoa.server.domain.card.entity.QCard.card;
import static com.amoa.server.domain.card.entity.QUserCard.userCard;
import static com.amoa.server.domain.card.entity.mapping.QCardDesignTag.cardDesignTag;
import static com.amoa.server.domain.common.entity.QRegion.region;
import static com.amoa.server.domain.shop.entity.QShop.shop;
import static com.amoa.server.domain.user.entity.mapping.QUserDesignTag.userDesignTag;
import static com.amoa.server.domain.user.entity.mapping.QUserRegion.userRegion;

@RequiredArgsConstructor
public class UserCardRepositoryImpl
        implements UserCardRepositoryCustom {

    private final JPAQueryFactory queryFactory;


    @Override
    public Page<UserCard> findLikedCardsByUserOrderByRecommended(
            User user,
            Pageable pageable
    ) {

        List<UserCard> content =
                queryFactory
                        .selectFrom(userCard)
                        .join(userCard.card, card).fetchJoin()
                        .join(card.shop, shop).fetchJoin()
                        .join(shop.region, region).fetchJoin()
                        .where(
                                userCard.user.eq(user)
                        )
                        .orderBy(
                                locationScore(user).desc(),
                                designScore(user).desc(),
                                card.likeCard.desc(),
                                userCard.createdAt.desc()
                        )
                        .offset(pageable.getOffset())
                        .limit(pageable.getPageSize())
                        .fetch();


        Long total =
                queryFactory
                        .select(userCard.count())
                        .from(userCard)
                        .where(
                                userCard.user.eq(user)
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

    private NumberExpression<Long> designScore(User user) {

        return Expressions.numberTemplate(
                Long.class,
                "({0})",
                JPAExpressions
                        .select(cardDesignTag.count())
                        .from(cardDesignTag)
                        .join(userDesignTag)
                        .on(
                                userDesignTag.designTag.id
                                        .eq(cardDesignTag.designTag.id)
                        )
                        .where(
                                userDesignTag.user.eq(user),
                                cardDesignTag.card.eq(card)
                        )
        );
    }

}