package com.github.bpazy.zhuzhu;

/**
 * @author ziyuan
 */
public interface WebCrawler<T> {
    boolean shouldVisit(String url);

    /**
     * There should be no operations that are blocked for too long because the crawler thread pool is used
     */
    T visit(String url, byte[] content);

    void handle(T t);
}
