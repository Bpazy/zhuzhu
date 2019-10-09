package com.github.bpazy.zhuzhu;

/**
 * @author ziyuan
 * created on 2019/9/30
 */
public interface Crawler {
    void start(Class<? extends WebCrawler> webCrawlerClass);
}
