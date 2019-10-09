package com.github.bpazy.zhuzhu.redis;

import org.junit.jupiter.api.Test;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author ziyuan
 * created on 2019/10/6
 */
public class JedisProxyTest {

    @Test
    public void test() {
        JedisProxy jedisProxy = new JedisProxy(new JedisPool("127.0.0.1", 6379));
        String testKey = "testKey";
        String testValue = "testValue";
        Jedis proxiedJedis = jedisProxy.getJedis();
        proxiedJedis.set(testKey, testValue);
        assertThat(proxiedJedis.get(testKey)).isEqualTo(testValue);
    }
}
