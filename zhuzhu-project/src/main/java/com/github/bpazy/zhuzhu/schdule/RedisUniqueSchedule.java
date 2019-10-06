package com.github.bpazy.zhuzhu.schdule;

import com.github.bpazy.zhuzhu.redis.JedisProxy;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

/**
 * @author ziyuan
 */
public class RedisUniqueSchedule implements Schedule {
    private final JedisProxy jedisProxy;
    private static final String VISITED = "1";
    private final Jedis jedis;

    /**
     * @param host    redis server host
     * @param port    redis server port
     * @param clearDB whether of not flushDB when crawler start
     */
    public RedisUniqueSchedule(String host, int port, boolean clearDB) {
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
        return "zhuzhu:" + url;
    }

    /**
     * pending list prefix
     */
    protected String getListKey() {
        return "zhuzhu:list";
    }

    public void close() {
        jedisProxy.close();
    }
}
