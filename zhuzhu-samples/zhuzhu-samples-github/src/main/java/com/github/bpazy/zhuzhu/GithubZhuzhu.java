package com.github.bpazy.zhuzhu;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

/**
 * @author ziyuan
 */
public class GithubZhuzhu {

    public static void main(String[] args) {
        CrawlerController controller = new CrawlerController();
        controller.addSeed("https://github.com/Bpazy");
        controller.start(MyWebCrawler.class);
    }

    @Slf4j
    public static class MyWebCrawler implements WebCrawler {
        @Override
        public boolean shouldVisit(String url) {
            return url.contains("https://github.com");
        }

        @Override
        @SneakyThrows
        public void visit(String url, byte[] content) {
            Document doc = Jsoup.parse(new String(content));
            log.info("visit {}: {}", doc.title(), url);
        }
    }
}
