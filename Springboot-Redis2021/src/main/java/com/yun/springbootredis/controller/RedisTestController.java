package com.yun.springbootredis.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("redis")
public class RedisTestController {

    @Autowired
    private RedisTemplate redisTemplate;

    @GetMapping("/test")
    public String testRedis(){
        ValueOperations ops = redisTemplate.opsForValue();
        ops.set("name","z3");
        String name = (String) ops.get("name");

        return name;
    }
}
