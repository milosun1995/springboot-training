package com.training.admin.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.training.admin.vo.ProfileVO;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;

/**
 * Redis 缓存配置（分布式缓存，支持集群）
 * 
 * 缓存策略：
 * - 用户权限缓存：cacheName = "userPermissions", key = "#userId", TTL = 30分钟
 * - 用户个人信息缓存：cacheName = "userProfile", key = "#username", TTL = 30分钟
 * - 角色权限缓存：cacheName = "rolePermissions", key = "#roleId", TTL = 30分钟
 * 
 * 优势：
 * - 支持集群部署（所有节点共享缓存）
 * - 缓存一致性更好
 * - 支持缓存失效通知
 * - 精确的缓存清除策略（只清除受影响用户）
 */
@Configuration
@EnableCaching
public class CacheConfig {

    @Bean
    public CacheManager cacheManager(RedisConnectionFactory connectionFactory) {
        // 配置 ObjectMapper，支持 Java 8 时间类型
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());  // 启用 Java 8 时间支持
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);  // 禁用时间戳格式，使用 ISO-8601 格式
        
        // 为 ProfileVO 创建专用的序列化器（类型安全）
        // 使用 Jackson2JsonRedisSerializer 显式指定类型，避免反序列化为 LinkedHashMap
        Jackson2JsonRedisSerializer<ProfileVO> profileSerializer = new Jackson2JsonRedisSerializer<>(ProfileVO.class);
        profileSerializer.setObjectMapper(objectMapper);
        
        // 为其他缓存创建通用序列化器（用于 List<String> 等简单类型）
        GenericJackson2JsonRedisSerializer defaultSerializer = new GenericJackson2JsonRedisSerializer(objectMapper);
        
        // 配置默认缓存策略（用于 userPermissions 等简单类型）
        RedisCacheConfiguration defaultConfig = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(30))  // 默认 TTL：30 分钟
                .serializeKeysWith(RedisSerializationContext.SerializationPair
                        .fromSerializer(new StringRedisSerializer()))  // Key 序列化：String
                .serializeValuesWith(RedisSerializationContext.SerializationPair
                        .fromSerializer(defaultSerializer))  // Value 序列化：JSON（支持 Java 8 时间）
                .disableCachingNullValues();  // 不缓存 null 值
        
        // 配置 userProfile 缓存的专用策略（使用类型安全的序列化器）
        RedisCacheConfiguration profileConfig = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(30))  // TTL：30 分钟
                .serializeKeysWith(RedisSerializationContext.SerializationPair
                        .fromSerializer(new StringRedisSerializer()))  // Key 序列化：String
                .serializeValuesWith(RedisSerializationContext.SerializationPair
                        .fromSerializer(profileSerializer))  // Value 序列化：ProfileVO（类型安全）
                .disableCachingNullValues();  // 不缓存 null 值

        // 创建缓存管理器，为不同缓存配置不同的序列化器
        return RedisCacheManager.builder(connectionFactory)
                .cacheDefaults(defaultConfig)  // 设置默认配置（用于简单类型）
                .withCacheConfiguration("userProfile", profileConfig)  // userProfile 使用专用配置
                .transactionAware()  // 支持事务
                .build();
    }
}
