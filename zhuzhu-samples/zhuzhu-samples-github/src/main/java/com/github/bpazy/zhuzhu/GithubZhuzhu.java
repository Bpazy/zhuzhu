package com.github.bpazy.zhuzhu;

import com.github.bpazy.zhuzhu.schdule.RedisUniqueSchedule;
import com.google.common.collect.Lists;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpHost;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author ziyuan
 */
public class GithubZhuzhu {


    public static void main(String[] args) {
        Crawlers.custom()
                .seeds(Lists.newArrayList("https://github.com/Bpazy/zhuzhu"))
                .proxy(new HttpHost("127.0.0.1", 8889))
                .threadNum(5)
                .schedule(new RedisUniqueSchedule("127.0.0.1", 6379, true))
                .build()
                .start(MyWebCrawler.class);
    }

    @Slf4j
    public static class MyWebCrawler implements WebCrawler {
        private static Pattern pattern = Pattern.compile("https://github\\.com/(\\w+)/(\\w+)$");

        @Override
        public boolean shouldVisit(String url) {
            return url.contains("https://github.com");
        }

        @Override
        @SneakyThrows
        public void visit(String url, byte[] content) {
            Matcher matcher = pattern.matcher(url);
            if (!matcher.find()) return;

            Document doc = Jsoup.parse(new String(content));
            Element titleElement = doc.selectFirst("div > h1 > strong > a");
            Element starElement = doc.selectFirst(".social-count.js-social-count");
            if (titleElement == null || starElement == null) return;

            log.info("repo: {}, star number: {}, url: {}", titleElement.text(), starElement.text(), url);
        }
    }
}
