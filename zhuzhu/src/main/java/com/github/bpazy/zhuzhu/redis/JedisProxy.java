package com.github.bpazy.zhuzhu.redis;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

/**
 * @author ziyuan
 * created on 2019/10/6
 */
public class JedisProxy {
    private JedisPool jedisPool;

    public JedisProxy(JedisPool jedisPool) {
        this.jedisPool = jedisPool;
    }

    public Jedis getJedis() {
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(Jedis.class);
        enhancer.setCallback((MethodInterceptor) (o, method, args, methodProxy) -> {
            Jedis resource = jedisPool.getResource();
            Object ret = methodProxy.invoke(resource, args);
            resource.close();
            return ret;
        });
        return (Jedis) enhancer.create();
    }

    public void close() {
        jedisPool.close();
    }
}
