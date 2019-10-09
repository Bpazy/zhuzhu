package com.github.bpazy.zhuzhu;

import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;


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


        String baseUrl2 = "https://github.com/test";
        String relativePath = "/1.html";
        List<String> urls2 = Utils.extractUrls(baseUrl2 + relativePath, extractUrlsHtml.getBytes(), StandardCharsets.UTF_8.toString());

        String[] expectedUrls2 = new String[]{
                "https://www.baidu.com",
                baseUrl2 + "/a.html",
                baseUrl2 + "/b.img",
        };
        assertThat(expectedUrls2).isEqualTo(urls2.toArray());
    }
}