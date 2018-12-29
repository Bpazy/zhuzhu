package com.github.bpazy.zhuzhu;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * TODO
 *
 * @author ziyuan
 */
public class GiteeZhuzhu {


    public static void main(String[] args) {
        CrawlerController controller = new CrawlerController();
        controller.addSeed("https://gitee.com/lemur/easypoi");
        controller.setThreadNum(10);
        controller.start(MyWebCrawler.class);
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
