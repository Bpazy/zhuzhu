package com.github.bpazy.zhuzhu;

import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;


/**
 * @author ziyuan
 */
public class UtilsTest {

    @Test
    public void extractUrlsTest() throws IOException {
        URL extractUrlsHtmlUrl = this.getClass().getClassLoader().getResource("util_test_extract_urls.html");
        assertThat(extractUrlsHtmlUrl).isNotNull();

        String extractUrlsHtml = IOUtils.toString(extractUrlsHtmlUrl, StandardCharsets.UTF_8);
        String baseUrl = "https://github.com";
        List<String> urls = Utils.extractUrls(baseUrl, extractUrlsHtml.getBytes(), StandardCharsets.UTF_8.toString());

        String[] expectedUrls = new String[]{
                "https://www.baidu.com",
                baseUrl + "/a.html",
                baseUrl + "/b.img",
        };
        assertThat(expectedUrls).isEqualTo(urls.toArray());
    }

    @Test
    public void getBaseUrlTest() throws MalformedURLException {
        String[] urls = new String[]{
                "https://github.com",
                "https://github.com/bpazy",
                "https://github.com/bpazy/test.html",
                "a://github.com/bpazy/test.html",
                "github.com/bpazy/test.html",
        };
        for (int i = 0; i < 3; i++) {
            assertThat(Utils.getBaseUrl(urls[i])).isEqualTo(urls[0]);
        }

        for (int i = 3; i < 5; i++) {
            int finalI = i;
            Throwable thrown = catchThrowable(() -> Utils.getBaseUrl(urls[finalI]));
            assertThat(thrown).isInstanceOf(MalformedURLException.class);
        }
    }
}