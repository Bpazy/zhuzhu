package com.github.bpazy.zhuzhu;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@UtilityClass
public class Util {

    public static List<String> extractUrls(byte[] contentBytes, String charset) {
        String content;
        try {
            content = IOUtils.toString(contentBytes, charset);
        } catch (IOException e) {
            log.error("{}", e);
            return Collections.emptyList();
        }
        Document doc = Jsoup.parse(content);
        return doc.select("a[href]")
                .eachAttr("abs:href").stream()
                .filter(u -> u.startsWith("http"))
                .collect(Collectors.toList());
    }
}
