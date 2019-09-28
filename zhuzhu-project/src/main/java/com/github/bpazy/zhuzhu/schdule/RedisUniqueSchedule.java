package com.github.bpazy.zhuzhu.schdule;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

/**
 * @author ziyuan
 */
public class RedisUniqueSchedule implements Schedule {
    private final JedisPool jedisPool;
    private static final String VISITED = "1";

    public RedisUniqueSchedule(String host, int port) {
        this(host, port, false);
    }

    /**
     * @param host    redis server host
     * @param port    redis server port
     * @param clearDB whether of not flushDB when crawler start
     */
    public RedisUniqueSchedule(String host, int port, boolean clearDB) {
        jedisPool = new JedisPool(host, port);
        if (clearDB) {
            clearDB();
        }
    }

    @Override
    public String take() {
        Jedis jedis = jedisPool.getResource();
        String url = jedis.lpop(getListKey());
        jedis.close();
        return url;
    }


    @Override
    public void add(String url) {
        Jedis jedis = jedisPool.getResource();
        if (jedis.setnx(getKey(url), VISITED) == 0) {
            return;
        }
        jedis.lpush(getListKey(), url);
        jedis.close();
    }

    public void close() {
        jedisPool.close();
    }

    public void clearDB() {
        Jedis jedis = jedisPool.getResource();
        jedis.del(getListKey());
        jedis.keys(getKey("*")).forEach(jedis::del);
        jedis.close();
    }

    /**
     * visited url key prefix
     */
    protected String getKey(String url) {
        return "zhuzhu:" + url;
    }

    /**
     * pending list prefix
     */
    protected String getListKey() {
        return "zhuzhu:list";
    }
}
