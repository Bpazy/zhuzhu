package com.github.bpazy.zhuzhu;

import com.github.bpazy.zhuzhu.schdule.Schedule;
import com.github.bpazy.zhuzhu.schdule.UniqueSchedule;
import lombok.Setter;
import lombok.SneakyThrows;
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
    @Setter
    private int threadNum = 1;
    @Setter
    private Schedule schedule;
    @Setter
    private HttpHost proxy;

    private static final int timeout = 3000;

    private CloseableHttpClient client = HttpClients.createDefault();
    private RequestConfig requestConfig;

    public CrawlerController() {
    }

    @SneakyThrows
    public void start(Class<? extends WebCrawler> webCrawlerClass) {
        ensureSchedule();
        ensureRequestConfig();

        ExecutorService executor = Executors.newFixedThreadPool(threadNum);
        WebCrawler webCrawler = new DefaultWebCrawlerFactory(webCrawlerClass).newInstance();
        while (true) {
            String url = schedule.take();
            if (StringUtils.isBlank(url)) {
                Thread.sleep(1000);
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

                    List<String> urls = Util.extractUrls(contentBytes, "UTF8");
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
//        log.info("Crawler stopped because of seeds is empty.");
    }

    private void ensureRequestConfig() {
        RequestConfig.Builder builder = RequestConfig.custom()
                .setCookieSpec(CookieSpecs.STANDARD)
                .setConnectTimeout(timeout)
                .setConnectionRequestTimeout(timeout)
                .setSocketTimeout(timeout);
        if (proxy != null) {
            builder.setProxy(proxy);
        }
        requestConfig = builder.build();
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

    public interface WebCrawlerFactory<T> {
        T newInstance();
    }

    public class DefaultWebCrawlerFactory implements WebCrawlerFactory<WebCrawler> {
        private Class<? extends WebCrawler> clazz;

        public DefaultWebCrawlerFactory(Class<? extends WebCrawler> clazz) {
            this.clazz = clazz;
        }

        @Override
        @SneakyThrows
        public WebCrawler newInstance() {
            return clazz.newInstance();
        }
    }
}
