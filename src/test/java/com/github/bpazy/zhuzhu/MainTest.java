package com.github.bpazy.zhuzhu;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.Test;

/**
 * @author ziyuan
 */
@Slf4j
public class MainTest {

    @Test
    public void testCrawlerController() {
        CrawlerController controller = new CrawlerController();
        controller.addSeed("https://www.baidu.com");
        controller.start(MyWebCrawler.class);
    }

    public static class MyWebCrawler implements WebCrawler {
        @Override
        public boolean shouldVisit(String url) {
            return url.startsWith("http") && url.contains("baidu.com");
        }

        @Override
        @SneakyThrows
        public void visit(String url, byte[] content) {
            Document doc = Jsoup.parse(new String(content));
            log.info("visit {}: {}", doc.title(), url);
        }
    }
}
