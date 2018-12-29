package com.github.bpazy.zhuzhu;

import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@UtilityClass
public class Util {

    public static List<String> extractUrls(String fullUrl, byte[] contentBytes, String charset) {
        if (StringUtils.isAnyBlank(fullUrl, charset)) return Collections.emptyList();
        fullUrl = fullUrl.endsWith("/") ? StringUtils.removeEnd(fullUrl, "/") : fullUrl;
        String relativeDomain = StringUtils.substringBeforeLast(fullUrl, "/");

        String domain = getDomain(fullUrl);
        String absoluteDomain = domain.endsWith("/") ? StringUtils.removeEnd(domain, "?") : domain;

        String content;
        try {
            content = IOUtils.toString(contentBytes, charset);
        } catch (IOException e) {
            log.error("{}", e);
            return Collections.emptyList();
        }
        Document doc = Jsoup.parse(content);
        Elements links = doc.select("a[href]");
        return links.eachAttr("href").stream()
                .filter(u -> !u.startsWith("javascript:"))
                .filter(u -> !u.startsWith("#"))
                .map(u -> {
                    if (u.startsWith("http")) return u;
                    if (u.startsWith("/")) return absoluteDomain + u;
                    return relativeDomain + "/" + u;
                })
                .collect(Collectors.toList());
    }


    @SneakyThrows
    public static String getDomain(String fullUrl) {
        URL url = new URL(fullUrl);
        return url.getProtocol() + "://" + url.getHost();
    }

    @SneakyThrows
    public static String normalizeUrl(String u) {
        URL url = new URL(u);
        String s = url.getProtocol() + "://" + url.getHost() + url.getPath();
        if (StringUtils.isNotBlank(url.getQuery())) {
            s = s + "?" + url.getQuery()
                    .replaceAll("\\s", "%20")
                    .replaceAll("/", "%2F")
                    .replaceAll("\\+", "%2B")
                    .replaceAll("\\?", "%3F")
                    .replaceAll("#", "%23");
            // TODO Exception in thread "pool-1-thread-1" java.lang.IllegalArgumentException: Illegal character in query at index 59: https://sv.baidu.com/videoui/page/videoland?pd=bjh&context={%22nid%22:%2214291184864727607922%22,%22sourceFrom%22:%22bjh%22}&fr=bjhauthor&type=video
        }
        if (StringUtils.isNoneEmpty(url.getRef())) {
            s = s + "#" + url.getRef();
        }
        return s;
    }
}