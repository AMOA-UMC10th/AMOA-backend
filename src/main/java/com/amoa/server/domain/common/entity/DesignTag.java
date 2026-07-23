package com.amoa.server.domain.common.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "design_tag")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class DesignTag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "design_tag_id")
    private Long id;

    @Column(name = "name", nullable = false, length = 50)
    private String name;

    @Column(name = "image_url", length = 500)
    private String imageUrl; // 온보딩에서 쓰이는 이미지

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
}