package com.github.bpazy.zhuzhu;

import com.github.bpazy.zhuzhu.schdule.Schedule;
import org.apache.http.Header;
import org.apache.http.HttpHost;

import java.util.List;

/**
 * @author ziyuan
 * created on 2019/9/30
 */
public class CrawlerControllerBuilder {
    private List<Header> headers;
    private HttpHost proxy;
    private int threadNum;
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

    public CrawlerControllerBuilder threadNum(int threadNum) {
        this.threadNum = threadNum;
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
        controller.setTimeout(timeout);
        controller.setSchedule(schedule);
        controller.setThreadNum(threadNum);
        controller.setProxy(proxy);
        if (seeds != null) {
            seeds.forEach(controller::addSeed);
        }
        return controller;
    }
}
