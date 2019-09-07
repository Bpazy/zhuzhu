package com.github.bpazy.zhuzhu;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

public class WebCrawlerFactoryTest {

    @Test
    public void newInstanceTest() {
        assertThat(new WebCrawlerFactory(NormalWebCrawler.class).newInstance())
                .isInstanceOf(NormalWebCrawler.class);

        assertThat(catchThrowable(() -> new WebCrawlerFactory(PrivateConstructWebCrawler.class).newInstance()))
                .hasCauseInstanceOf(IllegalAccessException.class);

        assertThat(catchThrowable(() -> new WebCrawlerFactory(WebCrawler.class).newInstance()))
                .hasCauseInstanceOf(InstantiationException.class);
    }

    /**
     * Should be instantiated
     */
    public static class NormalWebCrawler implements WebCrawler {
        @Override
        public boolean shouldVisit(String url) {
            return true;
        }

        @Override
        public void visit(String url, byte[] content) {
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
        public void visit(String url, byte[] content) {
        }
    }
}
