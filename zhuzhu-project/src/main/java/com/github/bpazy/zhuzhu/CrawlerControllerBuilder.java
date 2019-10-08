package com.github.bpazy.zhuzhu;

import com.github.bpazy.zhuzhu.schdule.Schedule;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.Header;
import org.apache.http.HttpHost;

import java.util.List;

/**
 * @author ziyuan
 * created on 2019/9/30
 */
@Slf4j
public class CrawlerControllerBuilder {
    private static final int DEFAULT_THREAD_NUMBER = 1;
    private static final int REQUEST_TIMEOUT = 3000;

    private List<Header> headers;
    private HttpHost proxy;
    private int crawlerThreadNum;
    private int handlerThreadNum;
    private int timeout;
    private Schedule schedule;
    private List<String> seeds;

    public CrawlerControllerBuilder headers(List<Header> headers) {
        this.headers = headers;
        return this;
    }

    public CrawlerControllerBuilder proxy(HttpHost proxy) {
        this.proxy = proxy;
        return this;
    }

    public CrawlerControllerBuilder crawlerThreadNum(int crawlerThreadNum) {
        this.crawlerThreadNum = crawlerThreadNum;
        return this;
    }

    public CrawlerControllerBuilder handlerThreadNum(int handlerThreadNum) {
        this.handlerThreadNum = handlerThreadNum;
        return this;
    }

    public CrawlerControllerBuilder timeout(int timeout) {
        this.timeout = timeout;
        return this;
    }

    public CrawlerControllerBuilder schedule(Schedule schedule) {
        this.schedule = schedule;
        return this;
    }

    public CrawlerControllerBuilder seeds(List<String> seeds) {
        this.seeds = seeds;
        return this;
    }

    public Crawler build() {
        CrawlerController controller = new CrawlerController();
        if (headers != null) {
            controller.getHeaders().addAll(headers);
        }
        if (timeout <= 0) {
            log.warn("timeout should be greater than 0. The timeout is now set to {}ms.", REQUEST_TIMEOUT);
            timeout = REQUEST_TIMEOUT;
        }
        controller.setTimeout(timeout);
        controller.setSchedule(schedule);
        if (crawlerThreadNum < DEFAULT_THREAD_NUMBER) {
            log.warn("Thread number should be a positive integer. The thread number is now set to {}.", DEFAULT_THREAD_NUMBER);
            crawlerThreadNum = DEFAULT_THREAD_NUMBER;
        }
        if (handlerThreadNum < DEFAULT_THREAD_NUMBER) {
            log.warn("Thread number should be a positive integer. The thread number is now set to {}.", DEFAULT_THREAD_NUMBER);
            handlerThreadNum = DEFAULT_THREAD_NUMBER;
        }
        controller.setCrawlerThreadNum(crawlerThreadNum);
        controller.setHandlerThreadNum(handlerThreadNum);
        controller.setProxy(proxy);
        if (seeds != null) {
            seeds.forEach(controller::addSeed);
        }
        return controller;
    }
}
