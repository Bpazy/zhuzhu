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

    /**
     * @param host    redis server host
     * @param port    redis server port
     * @param clearDB whether of not flushDB when crawler start
     */
    public RedisUniqueSchedule(String host, int port, boolean clearDB) {
        jedisProxy = new JedisProxy(new JedisPool(host, port));
        if (clearDB) {
            clearDB();
        }
    }

    @Override
    public String take() {
        return jedisProxy.getJedis().lpop(getListKey());
    }


    @Override
    public void add(String url) {
        if (setnx(url)) {
            return;
        }
        jedisProxy.getJedis().lpush(getListKey(), url);
    }

    private boolean setnx(String url) {
        return jedisProxy.getJedis().setnx(getKey(url), VISITED) == 0;
    }

    public void clearDB() {
        Jedis jedis = jedisProxy.getJedis();
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
