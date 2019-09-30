package com.github.bpazy.zhuzhu;

import com.github.bpazy.zhuzhu.schdule.Schedule;
import com.github.bpazy.zhuzhu.schdule.UniqueSchedule;
import com.google.common.collect.Lists;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.Header;
import org.apache.http.HttpHost;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * TODO setSchedule before addSeed. Maybe builder is necessary.
 *
 * @author ziyuan
 */
@Slf4j
public class CrawlerController implements Crawler {
    private static final int DEFAULT_THREAD_NUMBER = 1;
    private static final int REQUEST_TIMEOUT = 3000;

    @Setter
    @Getter
    private int threadNum;
    @Setter
    @Getter
    private Schedule schedule;
    @Setter
    @Getter
    private HttpHost proxy;
    @Setter
    @Getter
    private int timeout;
    @Setter
    @Getter
    private List<Header> headers;
    @Getter
    private List<String> seeds = Lists.newArrayList();

    private CloseableHttpClient client = HttpClients.createDefault();
    private RequestConfig requestConfig;

    public CrawlerController() {
    }

    @Override
    public void start(Class<? extends WebCrawler> webCrawlerClass) {
        init();

        ThreadPoolExecutor executor = new ThreadPoolExecutor(0, threadNum, 10, TimeUnit.SECONDS, new LinkedBlockingQueue<>());
        WebCrawler webCrawler = new WebCrawlerFactory(webCrawlerClass).newInstance();
        while (true) {
            String url = schedule.take();
            if (StringUtils.isBlank(url)) {
                // exit when schedule is empty and thread poll is empty
                if (executor.getPoolSize() == 0) {
                    break;
                }

                try {
                    // TODO schedule.wait()
                    Thread.sleep(timeout);
                } catch (InterruptedException e) {
                    log.error("", e);
                    break;
                }
                continue;
            }
            executor.execute(() -> {
                HttpGet httpGet = new HttpGet(url);
                headers.forEach(httpGet::addHeader);
                httpGet.setConfig(requestConfig);
                try (CloseableHttpResponse response = client.execute(httpGet)) {
                    byte[] contentBytes = null;
                    try {
                        contentBytes = IOUtils.toByteArray(response.getEntity().getContent());
                    } catch (IOException e) {
                        log.error("", e);
                    }
                    if (contentBytes == null) return;

                    List<String> urls = Utils.extractUrls(Utils.getBaseUrl(url), contentBytes, "UTF8");
                    urls.stream()
                            .map(String::trim)
                            .filter(webCrawler::shouldVisit)
                            .peek(u -> log.debug("will visit {}", u))
                            .forEach(schedule::add);

                    webCrawler.visit(url, contentBytes);
                } catch (IOException e) {
                    log.warn("can not read {}", httpGet.getURI());
                }
            });
        }
        log.info("Crawler finished.");
    }

    private void init() {
        if (threadNum < DEFAULT_THREAD_NUMBER) {
            log.warn("Thread number should be a positive integer. The thread number is now set to {}.", DEFAULT_THREAD_NUMBER);
            threadNum = DEFAULT_THREAD_NUMBER;
        }
        if (timeout <= 0) {
            log.warn("timeout should be greater than 0. The timeout is now set to {}ms.", REQUEST_TIMEOUT);
            timeout = REQUEST_TIMEOUT;
        }

        // 初始化requestConfig
        RequestConfig.Builder builder = RequestConfig.custom()
                .setCookieSpec(CookieSpecs.STANDARD)
                .setConnectTimeout(timeout)
                .setConnectionRequestTimeout(timeout)
                .setSocketTimeout(timeout);
        if (proxy != null) {
            builder.setProxy(proxy);
        }
        requestConfig = builder.build();

        ensureSchedule();
    }

    private void ensureSchedule() {
        if (this.schedule == null) {
            schedule = new UniqueSchedule();
        }
    }

    public void addSeed(String url) {
        ensureSchedule();
        this.seeds.add(url);

        // add seeds to schedule
        this.seeds.forEach(schedule::add);
    }

}
