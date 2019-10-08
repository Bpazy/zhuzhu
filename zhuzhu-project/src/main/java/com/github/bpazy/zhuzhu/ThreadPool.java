package com.github.bpazy.zhuzhu;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author ziyuan
 * created on 2019/10/8
 */
public class ThreadPool {
    private int crawlerThreadNum;
    private int handlerThreadNum;

    public ThreadPool(int crawlerThreadNum, int handlerThreadNum) {
        this.crawlerThreadNum = crawlerThreadNum;
        this.handlerThreadNum = handlerThreadNum;
    }

    public ThreadPoolExecutor getCrawlerThreadPoolExecutor() {
        ThreadPoolExecutor executor = new ThreadPoolExecutor(
                crawlerThreadNum, crawlerThreadNum,
                10, TimeUnit.SECONDS,
                new SynchronousQueue<>(true), new DefaultThreadFactory("crawler", "thread"),
                new BlockingPolicy());
        executor.allowCoreThreadTimeOut(true);
        return executor;
    }

    public ThreadPoolExecutor getHandlerThreadPoolExecutor() {
        ThreadPoolExecutor executor = new ThreadPoolExecutor(
                handlerThreadNum, handlerThreadNum,
                10, TimeUnit.SECONDS,
                new SynchronousQueue<>(true), new DefaultThreadFactory("handler", "thread"),
                new BlockingPolicy());
        executor.allowCoreThreadTimeOut(true);
        return executor;
    }

    /**
     * Block when thread pool work queue is full.
     * Usually used with SynchronousQueue.
     *
     * @see SynchronousQueue
     */
    static class BlockingPolicy implements RejectedExecutionHandler {
        @Override
        public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
            try {
                executor.getQueue().put(r);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * The default thread factory
     */
    static class DefaultThreadFactory implements ThreadFactory {
        private final ThreadGroup group;
        private final AtomicInteger threadNumber = new AtomicInteger(1);
        private final String namePrefix;

        DefaultThreadFactory(String poolNamePrefix, String threadNamePrefix) {
            SecurityManager s = System.getSecurityManager();
            group = (s != null) ? s.getThreadGroup() : Thread.currentThread().getThreadGroup();
            namePrefix = poolNamePrefix + "-" + threadNamePrefix + "-";
        }

        public Thread newThread(Runnable r) {
            Thread t = new Thread(group, r, namePrefix + threadNumber.getAndIncrement(), 0);
            if (t.isDaemon())
                t.setDaemon(false);
            if (t.getPriority() != Thread.NORM_PRIORITY)
                t.setPriority(Thread.NORM_PRIORITY);
            return t;
        }
    }
}
