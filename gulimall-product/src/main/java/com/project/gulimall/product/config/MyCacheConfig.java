package com.project.gulimall.product.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.cache.CacheProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.cache.RedisCache;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.cache.RedisCacheWriter;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;
import java.util.Map;

@EnableConfigurationProperties(CacheProperties.class)
@Configuration
@EnableCaching
public class MyCacheConfig {

    @Autowired
    private CacheProperties cacheProperties;
    @Value("${spring.cache.redis.null-value-ttl:5m}")
    private Duration nullValueTtl;

    // 定义不同缓存的TTL
    private Map<String, Duration> cacheTtls = Map.of(
            "category", Duration.ofHours(6), // 分类缓存6小时
            "product", Duration.ofMinutes(30), // 商品缓存30分钟
            "brand", Duration.ofHours(2), // 品牌缓存2小时
            "attr", Duration.ofHours(4) // 属性缓存4小时
    );

    @Bean
    @Primary
    RedisCacheConfiguration redisCacheConfiguration() {
        RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig();
        config = config.serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()));
        config = config.serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(new GenericJackson2JsonRedisSerializer()));
        // 让配置文件中的数据生效
        CacheProperties.Redis redisProperties = cacheProperties.getRedis();
        if (redisProperties.getTimeToLive() != null) {
            config = config.entryTtl(redisProperties.getTimeToLive());
        }

        if (redisProperties.getKeyPrefix() != null) {
            config = config.prefixCacheNameWith(redisProperties.getKeyPrefix());
        }

        if (!redisProperties.isCacheNullValues()) {
            config = config.disableCachingNullValues();
        }

        if (!redisProperties.isUseKeyPrefix()) {
            config = config.disableKeyPrefix();
        }
        return config;
    }

    @Bean
    @Primary
    public CacheManager cacheManager(RedisConnectionFactory connectionFactory) {
        RedisCacheWriter writer = RedisCacheWriter.nonLockingRedisCacheWriter(connectionFactory);
        RedisCacheConfiguration redisCacheConfiguration = redisCacheConfiguration();
        // null 缓存 TTL（可配置化，先写死 5 分钟）
        // Duration nullValueTtl = Duration.ofMinutes(5);

        return new RedisCacheManager(writer, redisCacheConfiguration) {
            /*@Override
            protected RedisCache createRedisCache(String name, RedisCacheConfiguration cacheConfig) {
                return new CustomRedisCache(name, writer, cacheConfig, nullValueTtl);
            }*/
            @Override
            protected RedisCache createRedisCache(String name, RedisCacheConfiguration cacheConfig) {
                // 为特定缓存创建自定义配置
                Duration specificTtl = cacheTtls.getOrDefault(name, cacheConfig.getTtl());
                RedisCacheConfiguration specificConfig = cacheConfig.entryTtl(specificTtl);
                return new CustomRedisCache(name, writer, specificConfig, nullValueTtl);
            }
        };
    }
}
