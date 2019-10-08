package com.github.bpazy.zhuzhu;

import org.junit.jupiter.api.Test;

import java.util.concurrent.ThreadPoolExecutor;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author ziyuan
 * created on 2019/10/8
 */
class ThreadPoolTest {

    @Test
    void threadPool() {
        ThreadPool threadPool = new ThreadPool(1, 1);
        ThreadPoolExecutor crawlerThreadPoolExecutor = threadPool.getCrawlerThreadPoolExecutor();
        ThreadPoolExecutor handlerThreadPoolExecutor = threadPool.getHandlerThreadPoolExecutor();
        assertThat(crawlerThreadPoolExecutor).isNotNull();
        assertThat(handlerThreadPoolExecutor).isNotNull();
    }
}