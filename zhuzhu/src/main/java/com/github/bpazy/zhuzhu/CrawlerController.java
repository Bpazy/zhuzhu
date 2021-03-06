package com.github.bpazy.zhuzhu;

import com.github.bpazy.zhuzhu.http.Header;
import com.github.bpazy.zhuzhu.http.HttpClient;
import com.github.bpazy.zhuzhu.http.RequestConfig;
import com.github.bpazy.zhuzhu.schdule.Schedule;
import com.google.common.collect.Lists;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author ziyuan
 */
@Slf4j
public class CrawlerController implements Crawler {

    @Setter
    @Getter
    private int crawlerThreadNum;
    @Setter
    @Getter
    private int handlerThreadNum;
    @Setter
    @Getter
    private Schedule schedule;
    @Setter
    @Getter
    private List<Header> headers = Lists.newArrayList();
    @Getter
    private List<String> seeds = Lists.newArrayList();
    @Setter
    private RequestConfig requestConfig;
    @Setter
    private HttpClient httpClient;

    @Override
    public void start(Class<? extends WebCrawler> webCrawlerClass) {
        log.info("Crawler start");

        ThreadPool threadPool = new ThreadPool(crawlerThreadNum, handlerThreadNum);
        ThreadPoolExecutor crawlerThreadPoolExecutor = threadPool.getCrawlerThreadPoolExecutor();
        ThreadPoolExecutor handlerThreadPoolExecutor = threadPool.getHandlerThreadPoolExecutor();
        WebCrawler webCrawler = new WebCrawlerFactory(webCrawlerClass).newInstance();

        while (true) {
            String url = schedule.take();
            if (StringUtils.isBlank(url)) {
                // exit when schedule is empty and thread poll is empty
                if (Monitors.activeSize() == 0 && schedule.size() == 0) {
                    break;
                }

                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    log.error("", e);
                    break;
                }
                continue;
            }
            crawlerThreadPoolExecutor.execute(Monitors.getRunnable(() -> {
                log.debug("Start crawling {}", url);
                try {
                    byte[] contentBytes = httpClient.get(url, requestConfig);
                    if (contentBytes == null) {
                        log.warn("skip {} because content bytes is null", url);
                        return;
                    }
                    List<String> urls = Utils.extractUrls(url, contentBytes, "UTF8");
                    urls.stream()
                            .map(String::trim)
                            .filter(webCrawler::shouldVisit)
                            .peek(u -> log.debug("Will visit {}", u))
                            .forEach(schedule::add);
                    schedule.markHandled(url);
                    Object ret = webCrawler.visit(url, contentBytes);
                    if (ret != null) {
                        handlerThreadPoolExecutor.execute(Monitors.getRunnable(() -> webCrawler.handle(ret)));
                    }
                } catch (IOException e) {
                    log.warn("Can not read {}, {}", url, e.getMessage());
                }
            }));
        }
        log.info("Crawler finished");
    }

    public void addSeed(String url) {
        this.seeds.add(url);

        // add seeds to schedule
        this.seeds.forEach(schedule::add);
    }

}
