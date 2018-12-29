package com.github.bpazy.zhuzhu.schdule;

/**
 * @author ziyuan
 */
public interface Schedule {
    boolean hasMore();

    String take();

    boolean unVisited(String url);

    void add(String url);
}
