package com.github.bpazy.zhuzhu.schdule;

import com.github.bpazy.zhuzhu.redis.JedisProxy;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

/**
 * @author ziyuan
 */
public class RedisUniqueSchedule implements Schedule {
    private static final String VISITED = "1";
    private final JedisProxy jedisProxy;
    private final Jedis jedis;
    private String flag;

    /**
     * @param host    redis server host
     * @param port    redis server port
     * @param clearDB whether of not flushDB when crawler start
     */
    public RedisUniqueSchedule(String host, int port, boolean clearDB) {
        this("default", host, port, clearDB);
    }

    /**
     * @param flag    used to distinguish between different domain names
     * @param host    redis server host
     * @param port    redis server port
     * @param clearDB whether of not flushDB when crawler start
     */
    public RedisUniqueSchedule(String flag, String host, int port, boolean clearDB) {
        this.flag = flag;
        jedisProxy = new JedisProxy(new JedisPool(host, port));
        jedis = jedisProxy.getJedis();
        if (clearDB) {
            clearDB();
        }
    }

    @Override
    public String take() {
        return jedis.lpop(getListKey());
    }


    @Override
    public void add(String url) {
        if (setnx(url)) {
            return;
        }
        jedis.lpush(getListKey(), url);
    }

    @Override
    public long size() {
        return jedis.llen(getListKey());
    }

    private boolean setnx(String url) {
        return jedis.setnx(getKey(url), VISITED) == 0;
    }

    public void clearDB() {
        jedis.del(getListKey());
        jedis.keys(getKey("*")).forEach(jedis::del);
    }

    /**
     * visited url key prefix
     */
    protected String getKey(String url) {
        return "zhuzhu:" + flag + url;
    }

    /**
     * pending list prefix
     */
    protected String getListKey() {
        return "zhuzhu:" + flag + ":list";
    }

    public void close() {
        jedisProxy.close();
    }
}
