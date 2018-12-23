package com.github.bpazy.zhuzhu;

import com.github.bpazy.zhuzhu.url.ZUrl;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import java.io.IOException;
import java.util.List;
import java.util.Set;

/**
 * @author ziyuan
 */
@Slf4j
public class CrawlerController {
    private CloseableHttpClient client = HttpClients.createDefault();
    private List<String> seeds = Lists.newArrayList();
    private Set<String> visited = Sets.newHashSet();
    private RequestConfig requestConfig;

    public CrawlerController() {
        int timeout = 3000;
        requestConfig = RequestConfig.custom()
                .setConnectTimeout(timeout)
                .setConnectionRequestTimeout(timeout)
                .setSocketTimeout(timeout)
                .build();
    }

    public void start(Class<? extends WebCrawler> webCrawlerClass) {
        WebCrawler webCrawler = new DefaultWebCrawlerFactory(webCrawlerClass).newInstance();
        while (seeds.size() > 0) {
            ZUrl zUrl;
            String seed = seeds.remove(0);
            try {
                zUrl = ZUrl.normalize(seed);
            } catch (IllegalArgumentException e) {
                log.warn("can not read {}", seed);
                continue;
            }
            HttpGet httpGet = new HttpGet(zUrl.getUrl());
            httpGet.setConfig(requestConfig);
            try (CloseableHttpResponse response = client.execute(httpGet)) {
                byte[] contentBytes = null;
                try {
                    contentBytes = IOUtils.toByteArray(response.getEntity().getContent());
                } catch (IOException e) {
                    log.error("{}", e);
                }
                if (contentBytes == null) continue;

                List<String> urls = Util.extractUrls(zUrl.getUrl(), contentBytes, "UTF8");
                urls.stream()
                        .map(String::trim)
                        .filter(visited::add)
                        .filter(webCrawler::shouldVisit)
                        .peek(u -> log.debug("shouldVisit {}", u))
                        .forEach(this::addSeed);

                webCrawler.visit(zUrl.getUrl(), contentBytes);
            } catch (IOException e) {
                log.warn("can not read {}", httpGet.getURI());
            }
        }
        log.info("Crawler stopped because of seeds is empty.");
    }

    public void addSeed(String url) {
        seeds.add(url);
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
