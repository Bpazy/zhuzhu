package com.github.bpazy.zhuzhu;

import com.github.bpazy.zhuzhu.http.DefaultHttpClient;
import com.github.bpazy.zhuzhu.http.Header;
import com.github.bpazy.zhuzhu.http.HttpClient;
import com.github.bpazy.zhuzhu.http.RequestConfig;
import com.github.bpazy.zhuzhu.schdule.Schedule;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * @author ziyuan
 * created on 2019/9/30
 */
@Slf4j
public class CrawlerControllerBuilder {
    private static final int DEFAULT_THREAD_NUMBER = 1;

    private List<Header> headers;
    private int crawlerThreadNum;
    private int handlerThreadNum;
    private Schedule schedule;
    private List<String> seeds;
    private HttpClient httpClient;
    private RequestConfig requestConfig;

    public CrawlerControllerBuilder headers(List<Header> headers) {
        this.headers = headers;
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

    public CrawlerControllerBuilder schedule(Schedule schedule) {
        this.schedule = schedule;
        return this;
    }

    public CrawlerControllerBuilder seeds(List<String> seeds) {
        this.seeds = seeds;
        return this;
    }

    public CrawlerControllerBuilder httpClient(HttpClient httpClient) {
        this.httpClient = httpClient;
        return this;
    }

    public CrawlerControllerBuilder requestConfig(RequestConfig requestConfig) {
        this.requestConfig = requestConfig;
        return this;
    }

    public Crawler build() {
        CrawlerController controller = new CrawlerController();
        ;
        if (httpClient == null) {
            controller.setHttpClient(new DefaultHttpClient());
        } else {
            controller.setHttpClient(httpClient);
        }

        if (headers != null) {
            controller.getHeaders().addAll(headers);
        }
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
        if (requestConfig != null) {
            controller.setRequestConfig(requestConfig);
        } else {
            controller.setRequestConfig(RequestConfig.builder().build());
        }

        if (seeds != null) {
            seeds.forEach(controller::addSeed);
        }
        return controller;
    }
}
