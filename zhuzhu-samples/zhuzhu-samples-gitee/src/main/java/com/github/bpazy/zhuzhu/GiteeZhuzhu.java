package com.github.bpazy.zhuzhu;

import com.github.bpazy.zhuzhu.schdule.UniqueSchedule;
import com.google.common.collect.Lists;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.message.BasicHeader;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author ziyuan
 */
public class GiteeZhuzhu {

    public static void main(String[] args) {
        Crawlers.custom()
                .seeds(Lists.newArrayList("https://gitee.com/lemur/easypoi"))
                .threadNum(5)
                .timeout(10000)
//                .schedule(new RedisUniqueSchedule("127.0.0.1", 6379, true))
                .schedule(new UniqueSchedule())
                .headers(Lists.newArrayList(
                        new BasicHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3"),
                        new BasicHeader("Accept-Encoding", "gzip, deflate, br"),
                        new BasicHeader("Accept-Language", "zh-CN,zh;q=0.9,en;q=0.8,zh-HK;q=0.7"),
                        new BasicHeader("Connection", "keep-alive"),
                        new BasicHeader("Host", "gitee.com"),
                        new BasicHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/77.0.3865.90 Safari/537.36")))
                .build()
                .start(MyWebCrawler.class);
    }

    @Slf4j
    public static class MyWebCrawler implements WebCrawler {
        private static Pattern pattern = Pattern.compile("https://gitee\\.com/(\\w+)/(\\w+)$");

        @Override
        public boolean shouldVisit(String url) {
            return url.contains("https://gitee.com");
        }

        @Override
        @SneakyThrows
        public void visit(String url, byte[] content) {
            Matcher matcher = pattern.matcher(url);
            if (!matcher.find()) return;

            Document doc = Jsoup.parse(new String(content));
            Element titleElement = doc.selectFirst("span.project-title > a.repository");
            Element starElement = doc.selectFirst(".star-container > a.ui.button.action-social-count");
            if (titleElement == null || starElement == null) return;

            log.info("repo: {}, star number: {}, url: {}", titleElement.text(), starElement.attr("title"), url);
        }
    }
}
