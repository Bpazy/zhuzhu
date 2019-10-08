package com.github.bpazy.zhuzhu.samples.cnwangku;

import com.github.bpazy.zhuzhu.Crawlers;
import com.github.bpazy.zhuzhu.WebCrawler;
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
 * created on 2019/10/7
 */
public class CnWangKu {

    public static void main(String[] args) {
        Crawlers.custom()
                .schedule(new RedisUniqueSchedule("127.0.0.1", 6379, true))
                .seeds(Lists.newArrayList("http://shop.99114.com/"))
                .crawlerThreadNum(5)
                .build()
                .start(MyWebCrawler.class);
    }

    @Slf4j
    public static class MyWebCrawler implements WebCrawler<RetObject> {
        private static Pattern pattern = Pattern.compile("http://shop\\.99114\\.com/(\\d+)$");

        @Override
        public boolean shouldVisit(String url) {
            return url.contains("http://shop.99114.com/");
        }

        @Override
        @SneakyThrows
        public RetObject visit(String url, byte[] content) {
            Matcher matcher = pattern.matcher(url);
            if (!matcher.find()) return null;

            Document doc = Jsoup.parse(new String(content));
            return RetObject.builder()
                    .title(doc.title())
                    .url(url)
                    .build();
        }

        @Override
        public void handle(RetObject retObject) {
            log.info("url: {}, title: {}", retObject.getUrl(), retObject.getTitle());
        }
    }
}
