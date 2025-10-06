package com.example.MyShop_API.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {

    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory factory) {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        // Thiết lập factory để RedisTemplate biết cách kết nối đến Redis server
        redisTemplate.setConnectionFactory(factory);
        // Đặt serializer cho key: String → byte[]
        redisTemplate.setKeySerializer(new StringRedisSerializer());

        // Đặt serializer cho value: String → byte[]
        redisTemplate.setValueSerializer(new GenericJackson2JsonRedisSerializer());

        return redisTemplate;
    }
}
