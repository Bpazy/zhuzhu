package com.github.bpazy.zhuzhu;

import org.junit.Test;

/**
 * @author ziyuan
 */
public class MainTest {

    @Test
    public void testCrawlerController() {
        CrawlerController controller = new CrawlerController();
        controller.addSeed("https://www.baidu.com");
        controller.start(WebCrawler.class);
    }
}
