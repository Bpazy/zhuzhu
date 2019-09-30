package com.github.bpazy.zhuzhu;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Utils the project has.<p>
 * Still unstable
 */
@Slf4j
public class Utils {

    /**
     * return all urls in body
     *
     * @param baseUrl      base url to build the complete url
     * @param contentBytes body bytes
     * @param charset      body charset to encode body bytes
     * @return all urls in body
     */
    public static List<String> extractUrls(String baseUrl, byte[] contentBytes, String charset) {
        String content;
        try {
            content = IOUtils.toString(contentBytes, charset);
        } catch (IOException e) {
            log.error("", e);
            return Collections.emptyList();
        }
        Document doc = Jsoup.parse(content, baseUrl);
        return doc.select("a[href]")
                .eachAttr("abs:href").stream()
                .filter(u -> u.startsWith("http"))
                .collect(Collectors.toList());
    }

    /**
     * Get base url from full url
     *
     * @param url full url
     * @return base url
     * @throws MalformedURLException if no protocol is specified, or an unknown protocol is found, or spec is null.
     */
    public static String getBaseUrl(String url) throws MalformedURLException {
        URL url1 = new URL(url);
        return url1.getProtocol() + "://" + url1.getHost();
    }
}
