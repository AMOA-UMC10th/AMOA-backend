package com.amoa.server.domain.common.entity;

import com.amoa.server.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
        name = "region",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_region",
                columnNames = {
                        "first_depth",
                        "second_depth",
                        "third_depth"
                }
        )
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Region extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "region_id")
    private Long id;

    // 1Depth (시/도)
    @Column(name = "first_depth", nullable = false, length = 50)
    private String firstDepth;

    // 2Depth (시/군/구)
    @Column(name = "second_depth", nullable = false, length = 50)
    private String secondDepth;

    // 3Depth (읍/면/동)
    @Column(name = "third_depth", nullable = false, length = 50)
    private String thirdDepth;
}