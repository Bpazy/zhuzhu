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
    public void threadPoolTest() {
        ThreadPool threadPool = new ThreadPool(1, 1);
        ThreadPoolExecutor crawlerThreadPoolExecutor = threadPool.getCrawlerThreadPoolExecutor();
        ThreadPoolExecutor handlerThreadPoolExecutor = threadPool.getHandlerThreadPoolExecutor();
        assertThat(crawlerThreadPoolExecutor).isNotNull();
        assertThat(handlerThreadPoolExecutor).isNotNull();

        Thread crawlerThread1 = crawlerThreadPoolExecutor.getThreadFactory().newThread(() -> {
        });
        Thread crawlerThread2 = crawlerThreadPoolExecutor.getThreadFactory().newThread(() -> {
        });
        Thread handlerThread1 = handlerThreadPoolExecutor.getThreadFactory().newThread(() -> {
        });
        Thread handlerThread2 = handlerThreadPoolExecutor.getThreadFactory().newThread(() -> {
        });
        assertThat(crawlerThread1.getName()).isEqualTo("crawler-thread-1");
        assertThat(crawlerThread2.getName()).isEqualTo("crawler-thread-2");
        assertThat(handlerThread1.getName()).isEqualTo("handler-thread-1");
        assertThat(handlerThread2.getName()).isEqualTo("handler-thread-2");
    }
}