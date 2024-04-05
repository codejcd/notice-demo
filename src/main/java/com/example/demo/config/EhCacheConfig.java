package com.example.demo.config;

import com.example.demo.notice.dto.NoticeResponse;
import org.ehcache.config.CacheConfiguration;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.jcache.JCacheCacheManager;
import org.springframework.context.annotation.Configuration;
import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;
import org.ehcache.jsr107.Eh107Configuration;
import org.springframework.context.annotation.Bean;
import javax.cache.Caching;

/**
 * Ehcache 설정
 */
@Configuration
@EnableCaching
public class EhCacheConfig {

    @Bean
    public org.springframework.cache.CacheManager ehCacheManager() {
        CacheConfiguration<Long, NoticeResponse> cacheConfig = CacheConfigurationBuilder
                .newCacheConfigurationBuilder(Long.class, NoticeResponse.class, ResourcePoolsBuilder.heap(10))
                .build();

        javax.cache.CacheManager cacheManager = Caching.getCachingProvider("org.ehcache.jsr107.EhcacheCachingProvider")
                .getCacheManager();

        String cacheName = "getNoticeCache";
        cacheManager.destroyCache(cacheName);
        cacheManager.createCache(cacheName, Eh107Configuration.fromEhcacheCacheConfiguration(cacheConfig));

        return new JCacheCacheManager(cacheManager);
    }
}
