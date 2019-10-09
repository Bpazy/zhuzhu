package com.github.bpazy.zhuzhu;


import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

public class WebCrawlerFactoryTest {

    @Test
    public void newInstanceTest() {
        WebCrawler<String> normalWebCrawler = new WebCrawlerFactory(NormalWebCrawler.class).newInstance();
        assertThat(normalWebCrawler).isInstanceOf(NormalWebCrawler.class);

        assertThat(catchThrowable(() -> new WebCrawlerFactory(PrivateConstructWebCrawler.class).newInstance()))
                .hasCauseInstanceOf(IllegalAccessException.class);

        assertThat(catchThrowable(() -> new WebCrawlerFactory(WebCrawler.class).newInstance()))
                .hasCauseInstanceOf(InstantiationException.class);

        assertThat(normalWebCrawler.shouldVisit("https://github.com/Bpazy/zhuzhu")).isTrue();
        String testData = "test data";
        assertThat(normalWebCrawler.visit("https://github.com/Bpazy/zhuzhu", testData.getBytes())).isEqualTo(testData);
        normalWebCrawler.handle(testData);
        assertThat(((NormalWebCrawler) normalWebCrawler).byteArrayOutputStream.toString()).isEqualTo(testData);
    }

    /**
     * Should be instantiated
     */
    public static class NormalWebCrawler implements WebCrawler<String> {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        @Override
        public boolean shouldVisit(String url) {
            return url.startsWith("https://github.com");
        }

        @Override
        public String visit(String url, byte[] content) {
            return new String(content);
        }

        @Override
        public void handle(String visitData) {
            try {
                byteArrayOutputStream.write(visitData.getBytes());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * Should throw IllegalAccessException
     */
    public static class PrivateConstructWebCrawler implements WebCrawler {
        private PrivateConstructWebCrawler() {
        }

        @Override
        public boolean shouldVisit(String url) {
            return true;
        }

        @Override
        public Object visit(String url, byte[] content) {
            return null;
        }

        @Override
        public void handle(Object o) {

        }
    }
}
