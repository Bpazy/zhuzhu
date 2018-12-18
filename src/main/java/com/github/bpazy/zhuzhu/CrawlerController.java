package com.github.bpazy.zhuzhu;

import com.google.common.collect.Lists;
import lombok.SneakyThrows;

import java.util.List;

/**
 * @author ziyuan
 */
public class CrawlerController {
    private List<String> seeds = Lists.newArrayList();

    public void start(Class<? extends WebCrawler> webCrawlerClass) {
        WebCrawler webCrawler = new DefaultWebCrawlerFactory(webCrawlerClass).newInstance();
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
