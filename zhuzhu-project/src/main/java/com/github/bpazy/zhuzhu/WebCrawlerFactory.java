package com.github.bpazy.zhuzhu;

/**
 * web crawler factory
 *
 * @author ziyuan
 * created on 2019/9/2
 */
public interface WebCrawlerFactory<T> {
    /**
     * instantiate web crawler
     *
     * @return new instance
     */
    T newInstance();
}
