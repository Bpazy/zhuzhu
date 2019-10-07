package com.github.bpazy.zhuzhu;

import com.github.bpazy.zhuzhu.schdule.Schedule;
import com.github.bpazy.zhuzhu.schdule.UniqueSchedule;
import com.google.common.collect.Lists;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.Header;
import org.apache.http.HttpHost;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

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

    @Setter
    @Getter
    private int threadNum;
    @Setter
    @Getter
    private Schedule schedule; // TODO Separate crawler threads and worker threads
    @Setter
    @Getter
    private HttpHost proxy;
    @Setter
    @Getter
    private int timeout;
    @Setter
    @Getter
    private List<Header> headers = Lists.newArrayList();
    @Getter
    private List<String> seeds = Lists.newArrayList();

    private CloseableHttpClient client = HttpClients.createDefault();
    private RequestConfig requestConfig;

    public CrawlerController() {
    }

    @Override
    public void start(Class<? extends WebCrawler> webCrawlerClass) {
        log.info("Crawler start");
        init();

        ThreadPoolExecutor executor = getThreadPoolExecutor();
        WebCrawler webCrawler = new WebCrawlerFactory(webCrawlerClass).newInstance();
        while (true) {
            // FIXME Control the rate of fetch, otherwise the thread pool queue will keep increasing
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
                log.debug("Start crawling {}", url);
                HttpGet httpGet = new HttpGet(url);
                headers.forEach(httpGet::addHeader);
                httpGet.setConfig(requestConfig);
                try (CloseableHttpResponse response = client.execute(httpGet)) {
                    byte[] contentBytes = EntityUtils.toByteArray(response.getEntity());
                    List<String> urls = Utils.extractUrls(url, contentBytes, "UTF8");
                    urls.stream()
                            .map(String::trim)
                            .filter(webCrawler::shouldVisit)
                            .peek(u -> log.debug("Will visit {}", u))
                            .forEach(schedule::add);

                    Object ret = webCrawler.visit(url, contentBytes);
                    if (ret != null) {
                        webCrawler.handle(ret);
                    }
                } catch (IOException e) {
                    log.warn("Can not read {}, {}", httpGet.getURI(), e.getMessage());
                }
            });
        }
        log.info("Crawler finished");
    }

    private ThreadPoolExecutor getThreadPoolExecutor() {
        ThreadPoolExecutor executor = new ThreadPoolExecutor(threadNum, threadNum, 10, TimeUnit.SECONDS, new LinkedBlockingQueue<>());
        executor.allowCoreThreadTimeOut(true);
        return executor;
    }

    private void init() {
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
