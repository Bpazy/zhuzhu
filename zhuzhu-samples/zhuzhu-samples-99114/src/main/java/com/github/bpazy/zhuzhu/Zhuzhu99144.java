package com.github.bpazy.zhuzhu;

import com.github.bpazy.zhuzhu.schdule.RedisUniqueSchedule;
import com.google.common.collect.Lists;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author ziyuan
 */
public class Zhuzhu99144 {

    public static void main(String[] args) {
        Crawlers.custom()
                .schedule(new RedisUniqueSchedule("127.0.0.1", 6379, true))
                .seeds(Lists.newArrayList("http://shop.99114.com/"))
                .threadNum(5)
                .build().start(MyWebCrawler.class);
    }

    @Slf4j
    public static class MyWebCrawler implements WebCrawler {
        private static Pattern pattern = Pattern.compile("http://shop\\.99114\\.com/(\\d+)$");

        @Override
        public boolean shouldVisit(String url) {
            return url.contains("http://shop.99114.com/");
        }

        @Override
        @SneakyThrows
        public void visit(String url, byte[] content) {
            Matcher matcher = pattern.matcher(url);
            if (!matcher.find()) return;

            Document doc = Jsoup.parse(new String(content));
            log.info("url: {}, title: {}", url, doc.title());
        }
    }
}
