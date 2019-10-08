package com.github.bpazy.zhuzhu;

import com.github.bpazy.zhuzhu.schdule.Schedule;
import com.github.bpazy.zhuzhu.schdule.UniqueSchedule;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.Header;
import org.apache.http.HttpHost;
import org.apache.http.message.BasicHeader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author ziyuan
 * created on 2019/9/30
 */
@Slf4j
public class CrawlersTest {
    private static final Object lock = new Object();
    private static volatile int flag = 0;
    private List<Header> testHeaders;
    private List<String> testSeeds;
    private HttpHost testProxy;
    private int testThreadNum;
    private int testTimeout;
    private UniqueSchedule testUniqueSchedule;

    // FIXME Broken CI
    //    @Test
    public void test() throws InterruptedException {
        new Thread(() -> getDefaultTestCrawlerController().start(TestWebCrawler.class)).start();

        synchronized (lock) {
            long start = System.currentTimeMillis();
            while (flag == 0) {
                int timeout = 5000;
                lock.wait(timeout);
                if (System.currentTimeMillis() - start > timeout) {
                    throw new RuntimeException("Crawler test failed. Maybe request to github.com timeout or program error");
                }
            }
        }
    }

    @BeforeEach
    void setUp() {
        testHeaders = Lists.newArrayList(new BasicHeader("name", "value"));
        testSeeds = Lists.newArrayList("https://github.com/Bpazy/zhuzhu"); // TODO maybe test http server is better
        testProxy = new HttpHost("127.0.0.1", 8889);
        testThreadNum = 5;
        testTimeout = 3000;
        testUniqueSchedule = new UniqueSchedule();
    }

    @Test
    public void buildCrawlerControllerTest() {
        CrawlerController controller = (CrawlerController) getDefaultTestCrawlerController();
        assertThat(controller.getHeaders()).isEqualTo(testHeaders);
        assertThat(controller.getProxy()).isEqualTo(testProxy);
        assertThat(controller.getCrawlerThreadNum()).isEqualTo(testThreadNum);
        assertThat(controller.getTimeout()).isEqualTo(testTimeout);
        assertThat(controller.getSchedule()).isEqualTo(testUniqueSchedule);
        assertThat(controller.getSeeds()).isEqualTo(testSeeds);
    }


    @Test
    public void testInnerFields() {
        List<Header> testHeaders = Lists.newArrayList(new BasicHeader("name", "value"));
        CrawlerControllerBuilder builder = Crawlers.custom()
                .headers(testHeaders)
                .proxy(testProxy)
                .crawlerThreadNum(-1)
                .handlerThreadNum(-1)
                .timeout(-1)
                .schedule(testUniqueSchedule)
                .seeds(testSeeds);
        CrawlerController crawler1 = (CrawlerController) builder.build();
        assertThat(crawler1.getCrawlerThreadNum()).isEqualTo(1);
        assertThat(crawler1.getTimeout()).isEqualTo(3000);
        assertThat(crawler1.getHeaders()).isEqualTo(testHeaders);

        CrawlerController defaultScheduleCrawler = (CrawlerController) Crawlers.custom().seeds(Lists.newArrayList("https://github.com")).build();
        assertThat(defaultScheduleCrawler.getSchedule()).isInstanceOf(UniqueSchedule.class);

        Schedule schedule = new TestSchedule();
        CrawlerController specifiedScheduleCrawler = (CrawlerController) Crawlers.custom().schedule(schedule).build();
        assertThat(specifiedScheduleCrawler.getSchedule()).isInstanceOf(TestSchedule.class);

        CrawlerController crawler2 = (CrawlerController) builder.crawlerThreadNum(Integer.MAX_VALUE).build();
        assertThat(crawler2.getCrawlerThreadNum()).isEqualTo(Integer.MAX_VALUE);
    }

    private Crawler getDefaultTestCrawlerController() {
        return Crawlers.custom()
                .headers(testHeaders)
                .proxy(testProxy)
                .crawlerThreadNum(testThreadNum)
                .timeout(testTimeout)
                .schedule(testUniqueSchedule)
                .seeds(testSeeds)
                .build();
    }

    @Slf4j
    public static class TestWebCrawler implements WebCrawler {

        @Override
        public boolean shouldVisit(String url) {
            boolean equals = "https://github.com/Bpazy/zhuzhu/blob/master/README.md".equals(url);
            if (equals) {
                synchronized (lock) {
                    lock.notifyAll();
                    flag = 1;
                }
            }
            return equals;
        }

        @Override
        public Object visit(String url, byte[] content) {
            return null;
        }

        @Override
        public void handle(Object o) {

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
    }
}
