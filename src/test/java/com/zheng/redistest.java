package com.zheng;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;

@SpringBootTest
public class redistest {
    @Autowired
    private RedisTemplate redisTemplate;
    @Test
    public void test_bitmap(){
        redisTemplate.opsForValue().setBit("1",20,false);
    }
}
