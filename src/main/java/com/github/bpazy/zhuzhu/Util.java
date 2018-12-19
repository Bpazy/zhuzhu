package com.github.bpazy.zhuzhu;

import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.net.URL;
import java.net.URLEncoder;
import java.util.List;

@UtilityClass
public class Util {

    @SneakyThrows
    public static List<String> extractUrls(byte[] contentBytes, String charset) {
        String content = IOUtils.toString(contentBytes, charset);
        Document doc = Jsoup.parse(content);
        Elements links = doc.select("a[href]");
        return links.eachAttr("href");
    }

    // TODO java.lang.IllegalArgumentException: Illegal character in authority at index 7: http://m.baidu.com			?uid%3D2FB5058F-9073-E7B9-0D0A-36C51C3D0883
    @SneakyThrows
    public static String normalizeUrl(String u) {
        URL url = new URL(u);
        String s = url.getProtocol() + "://" + url.getHost();
        if (StringUtils.isNotBlank(url.getQuery())) {
            return s + "?" + URLEncoder.encode(url.getQuery(), "UTF8");
        }
        return s;
    }
}
