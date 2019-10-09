package com.github.bpazy.zhuzhu;

import lombok.extern.slf4j.Slf4j;

/**
 * default web crawler factory.
 *
 * @author ziyuan
 * created on 2019/9/2
 */
@Slf4j
public class WebCrawlerFactory implements WebCrawlerInitializr<WebCrawler> {
    private Class<? extends WebCrawler> clazz;

    public WebCrawlerFactory(Class<? extends WebCrawler> clazz) {
        this.clazz = clazz;
    }

    @Override
    public WebCrawler newInstance() {
        try {
            return clazz.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            log.error("", e);
            throw new RuntimeException(e);
        }
    }
}
