package com.project.gulimall.product.config;

import org.springframework.data.redis.cache.RedisCache;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheWriter;
import java.time.Duration;

public class CustomRedisCache extends RedisCache {
    private final RedisCacheWriter cacheWriter;
    private final RedisCacheConfiguration cacheConfig;
    private final Duration nullValueTtl;
    private final Duration normalValueTtl; // 显式存储正常值的TTL

    public CustomRedisCache(String name, // 缓存的唯一标识符，对应 Redis 中的 key 前缀
                            RedisCacheWriter cacheWriter, // 真正执行 Redis 读写操作的核心组件
                            RedisCacheConfiguration cacheConfig, // 缓存的配置信息容器，包含所有序列化和行为配置
                            Duration nullValueTtl // 专门为 null 值设置的独立过期时间
    ) {
        super(name, cacheWriter, cacheConfig);

        // 验证配置
        System.out.println("传入的配置信息：");
        System.out.println("TTL: " + cacheConfig.getTtl());
        System.out.println("是否缓存null: " + cacheConfig.getAllowCacheNullValues());
        System.out.println("Key序列化器: " + cacheConfig.getKeySerializationPair());

        this.cacheWriter = cacheWriter;
        this.cacheConfig = cacheConfig;
        this.nullValueTtl = nullValueTtl;
        // 显式获取并存储正常值的TTL
        this.normalValueTtl = cacheConfig.getTtl();
    }

    @Override
    public void put(Object key, Object value) {
        if (value == null) {
            // 如果是 null，缓存 NullValue 占位符
            byte[] cacheKey = this.serializeCacheKey(key.toString());
            byte[] cacheValue = this.serializeCacheValue(NullValue.INSTANCE);
            cacheWriter.put(getName(), cacheKey, cacheValue, nullValueTtl);
        } else {
             super.put(key, value);
        }
    }

    /*@Override
    public void put(Object key, Object value) {
        byte[] cacheKey = this.serializeCacheKey(key.toString());
        byte[] cacheValue = this.serializeCacheValue(value == null ? NullValue.INSTANCE : value);

        Duration ttl = (value == null ? nullValueTtl : cacheConfig.getTtl());
        cacheWriter.put(getName(), cacheKey, cacheValue, ttl);
    }*/

    /*@Override
    public void put(Object key, Object value) {
        String cacheName = getName();
        byte[] cacheKey = serializeCacheKey(key.toString());
        byte[] cacheValue;
        Duration ttl;

        if (value == null) {
            // 处理null值
            cacheValue = serializeCacheValue(NullValue.INSTANCE);
            ttl = nullValueTtl;
            System.out.println("缓存空值: " + key + ", TTL: " + ttl);
        } else {
            // 处理正常值
            cacheValue = serializeCacheValue(value);
            ttl = normalValueTtl; // 使用显式存储的TTL
            System.out.println("缓存正常值: " + key + ", TTL: " + ttl);
        }

        // 确保TTL不为null或负数
        if (ttl == null || ttl.isNegative() || ttl.isZero()) {
            ttl = Duration.ofMinutes(10); // 默认值
        }

        cacheWriter.put(cacheName, cacheKey, cacheValue, ttl);
    }*/

    @Override
    protected Object lookup(Object key) {
        Object value = super.lookup(key);
        if (value instanceof NullValue) {
            return null;
        }
        return value;
    }
}
