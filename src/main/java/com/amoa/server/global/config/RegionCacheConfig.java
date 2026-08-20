package com.amoa.server.global.config;

import com.amoa.server.domain.common.entity.Region;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RegionCacheConfig {

    @Bean
    public Cache<String, List<Region>> regionCache() {
        return Caffeine.newBuilder()
                .maximumSize(1)
                .build();
    }
}
