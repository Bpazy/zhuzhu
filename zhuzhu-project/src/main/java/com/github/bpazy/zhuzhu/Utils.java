package com.github.bpazy.zhuzhu;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
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
     * @param baseUrl      The url of the current web page
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
}
