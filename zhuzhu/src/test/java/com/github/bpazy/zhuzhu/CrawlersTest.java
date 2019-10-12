package com.github.bpazy.zhuzhu;

import com.github.bpazy.zhuzhu.http.*;
import com.github.bpazy.zhuzhu.schdule.Schedule;
import com.github.bpazy.zhuzhu.schdule.UniqueSchedule;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author ziyuan
 * created on 2019/9/30
 */
@Slf4j
public class CrawlersTest {
    private List<Header> testHeaders;
    private List<String> testSeeds;
    private HttpHost testProxy;
    private int testThreadNum;
    private int testTimeout;
    private UniqueSchedule testUniqueSchedule;

    @BeforeEach
    void setUp() {
        testHeaders = Lists.newArrayList(new BasicHeader("name", "value"));
        testSeeds = Lists.newArrayList("https://github.com/Bpazy/zhuzhu");
        testProxy = new HttpHost("127.0.0.1", 8889);
        testThreadNum = 5;
        testTimeout = 3000;
        testUniqueSchedule = new UniqueSchedule();
    }

    @Test
    public void overall() throws IOException {
        RequestConfig config = RequestConfig.builder().setTimeout(100).build();
        HttpClient mockHttpClient = mock(HttpClient.class);

        String baseUrl = "https://github.com/Bpazy/zhuzhu/web{no}";
        String baseContent = "<title>title{titleNo}</title><a href=\"https://github.com/Bpazy/zhuzhu/web{no}\">url</a>";
        StringBuilder expectedTitle = new StringBuilder();
        for (int i = 0; i < 10; i++) {
            String currentUrl = baseUrl.replaceAll("\\{no}", i + "");
            String currentContent = baseContent.replaceAll("\\{titleNo}", i + "")
                    .replaceAll("\\{no}", i + 1 + "");
            when(mockHttpClient.get(currentUrl, config)).thenReturn(currentContent.getBytes());
            expectedTitle.append("title").append(i);
        }

        Crawlers.custom()
                .httpClient(mockHttpClient)
                .requestConfig(config)
                .seeds(Lists.newArrayList(baseUrl.replaceAll("\\{no}", 0 + "")))
                .build()
                .start(TestWebCrawler.class);

        assertThat(TestWebCrawler.byteArrayOutputStream.toString()).isEqualTo(expectedTitle.toString());
    }

    @Test
    public void buildCrawlerControllerTest() {
        CrawlerController controller = (CrawlerController) Crawlers.custom()
                .headers(testHeaders)
                .requestConfig(RequestConfig.builder()
                        .setProxy(testProxy)
                        .setTimeout(testTimeout)
                        .build())
                .crawlerThreadNum(testThreadNum)
                .schedule(testUniqueSchedule)
                .seeds(testSeeds)
                .build();

        assertThat(controller.getHeaders()).isEqualTo(testHeaders);
        assertThat(controller.getCrawlerThreadNum()).isEqualTo(testThreadNum);
        assertThat(controller.getSchedule()).isEqualTo(testUniqueSchedule);
        assertThat(controller.getSeeds()).isEqualTo(testSeeds);
    }


    @Test
    public void testInnerFields() {
        List<Header> testHeaders = Lists.newArrayList(new BasicHeader("name", "value"));
        CrawlerControllerBuilder builder = Crawlers.custom()
                .headers(testHeaders)
                .requestConfig(RequestConfig.builder()
                        .setProxy(testProxy)
                        .setTimeout(-1)
                        .build())
                .crawlerThreadNum(-1)
                .handlerThreadNum(-1)
                .schedule(testUniqueSchedule)
                .seeds(testSeeds);
        CrawlerController crawler1 = (CrawlerController) builder.build();
        assertThat(crawler1.getCrawlerThreadNum()).isEqualTo(1);
        assertThat(crawler1.getHeaders()).isEqualTo(testHeaders);

        Schedule schedule = new TestSchedule();
        CrawlerController specifiedScheduleCrawler = (CrawlerController) Crawlers.custom().schedule(schedule).build();
        assertThat(specifiedScheduleCrawler.getSchedule()).isInstanceOf(TestSchedule.class);

        CrawlerController crawler2 = (CrawlerController) builder.crawlerThreadNum(Integer.MAX_VALUE).build();
        assertThat(crawler2.getCrawlerThreadNum()).isEqualTo(Integer.MAX_VALUE);
    }

    @Slf4j
    public static class TestWebCrawler implements WebCrawler<String> {
        static ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        @Override
        public boolean shouldVisit(String url) {
            return true;
        }

        @Override
        public String visit(String url, byte[] content) {
            String title = Jsoup.parse(new String(content)).title();
            if (StringUtils.isNotEmpty(title)) {
                return title;
            }
            return null;
        }

        @Override
        public void handle(String o) {
            try {
                byteArrayOutputStream.write(o.getBytes());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static class TestSchedule implements Schedule {
        @Override
        public String take() {
            return null;
        }

        @Override
        public void add(String url) {

        }

        @Override
        public void markHandled(String url) {

        }

        @Override
        public long size() {
            return 0;
        }
    }
}
