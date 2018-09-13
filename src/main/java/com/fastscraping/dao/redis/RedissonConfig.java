package com.fastscraping.dao.redis;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;

public class RedissonConfig {
    private final RedissonClient reredissonClient;

    public RedissonConfig() {
        this.reredissonClient = Redisson.create();
    }

    RedissonClient getRedissonClient() {
        return reredissonClient;
    }
}
