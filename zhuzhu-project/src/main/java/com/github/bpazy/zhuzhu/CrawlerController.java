package com.github.bpazy.zhuzhu;

import com.github.bpazy.zhuzhu.schdule.Schedule;
import com.github.bpazy.zhuzhu.schdule.UniqueSchedule;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpHost;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author ziyuan
 */
@Slf4j
public class CrawlerController {
    private static final int DEFAULT_THREAD_NUMBER = 1;
    private static final int REQUEST_TIMEOUT = 3000;

    @Setter
    private int threadNum;
    @Setter
    private Schedule schedule;
    @Setter
    private HttpHost proxy;
    @Setter
    private int timeout;

    private CloseableHttpClient client = HttpClients.createDefault();
    private RequestConfig requestConfig;

    public CrawlerController() {
    }

    public void start(Class<? extends WebCrawler> webCrawlerClass) {
        init();

        ExecutorService executor = Executors.newFixedThreadPool(threadNum);
        WebCrawler webCrawler = new DefaultWebCrawlerFactory(webCrawlerClass).newInstance();
        while (true) {
            String url = schedule.take();
            if (StringUtils.isBlank(url)) {
                try {
                    Thread.sleep(timeout);
                } catch (InterruptedException e) {
                    log.error("", e);
                    break;
                }
                continue;
            }
            executor.execute(() -> {
                HttpGet httpGet = new HttpGet(url);
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
                    // TODO check crawler is finished
                } catch (IOException e) {
                    log.warn("can not read {}", httpGet.getURI());
                }
            });
        }
        log.info("Crawler stopped because of seeds is empty.");
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

        schedule.add(url);
    }

}
