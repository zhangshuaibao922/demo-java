package com.cardo.demojava.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

@Component
public class RedisService {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    public void setKeyValue(String key, String value) {
        stringRedisTemplate.opsForValue().set(key, value);
    }

    public String getValueByKey(String key) {
        return stringRedisTemplate.opsForValue().get(key);
    }
    public void deleteKey(String key) {
        stringRedisTemplate.delete(key);
    }
}