package com.github.bpazy.zhuzhu.spider;

import com.github.bpazy.reflections.Reflections;
import com.github.bpazy.zhuzhu.EngineOption;
import com.github.bpazy.zhuzhu.annotation.Bean;
import com.github.bpazy.zhuzhu.annotation.Html;
import com.github.bpazy.zhuzhu.annotation.Title;
import com.github.bpazy.zhuzhu.annotation.Url;
import com.github.bpazy.zhuzhu.pipeline.Pipelines;
import com.google.common.collect.Lists;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @author ziyuan
 */
@Slf4j
public class Spider implements Runnable {
    private HttpClient client;
    private EngineOption option;
    private Class clazz;

    public Spider(EngineOption option, List<Class> beans) {
        client = HttpClients.createDefault();
        this.option = option;
        this.clazz = beans.get(0);
    }

    @Override
    @SneakyThrows
    public void run() {
        spider(option.getStartUrl());
        while (true) {
            String url = option.getSchedule().out();
            if (StringUtils.isEmpty(url)) {
                Thread.sleep(1000);
            }
            spider(url);
        }
    }

    private void spider(String url) {
        try {
            Object instance = clazz.newInstance();
            Set<Field> allHtmlFields = Reflections.getAllFields(clazz);
            Bean bean = Reflections.getAnnotationByType(clazz, Bean.class);

            HttpGet request = new HttpGet(url);
            String content = EntityUtils.toString(client.execute(request).getEntity());
            Document doc = Jsoup.parse(content);

            for (Field field : allHtmlFields) {
                handleHtml(instance, doc, field);
                handleTitle(instance, doc, field);
                handleUrl(instance, url, field);
            }
            Pipelines.run(bean, instance);

            Set<String> allUrls = doc.select("a").stream()
                    .map(e -> getUrl(getBaseUrl(request), e.attr("href")))
                    .filter(StringUtils::isNotEmpty)
                    .collect(Collectors.toSet());
            String match = bean.match();
            Pattern pattern = Pattern.compile(match);
            for (String u : allUrls) {
                Matcher matcher = pattern.matcher(u);
                if (matcher.find()) {
                    option.getSchedule().in(u);
                }
            }

        } catch (Exception e) {
            log.error("{}", e);
        }
    }

    private void handleUrl(Object instance, String url, Field field) {
        if (!field.isAnnotationPresent(Url.class)) return;

        Reflections.setField(instance, field, url);
    }

    private void handleTitle(Object instance, Document doc, Field field) {
        if (!field.isAnnotationPresent(Title.class)) return;

        Reflections.setField(instance, field, doc.title());
    }

    private void handleHtml(Object instance, Document doc, Field field) {
        if (!field.isAnnotationPresent(Html.class)) return;

        Html htmlAnno = field.getAnnotation(Html.class);
        String selector = htmlAnno.selector();
        Elements elements = doc.select(selector);

        if (elements.size() == 1) {
            if (isList(field)) {
                Reflections.setField(instance, field, Lists.newArrayList(getElementAttrOrText(htmlAnno, elements.get(0))));
            } else {
                Reflections.setField(instance, field, getElementAttrOrText(htmlAnno, elements.get(0)));
            }
        } else if (elements.size() > 1) {
            if (isList(field)) {
                Reflections.setField(
                        instance,
                        field,
                        elements.stream()
                                .map(e -> getElementAttrOrText(htmlAnno, e).toString())
                                .collect(Collectors.toList()));
            } else {
                Reflections.setField(
                        instance,
                        field,
                        elements.stream()
                                .map(e -> getElementAttrOrText(htmlAnno, e).toString())
                                .reduce(String::concat));
            }
        }
    }

    private String getUrl(String baseUrl, String u) {
        if (u.startsWith("#")) return null;

        if (u.startsWith("/")) {
            if (baseUrl.endsWith("/")) {
                return baseUrl.substring(baseUrl.length() - 2, baseUrl.length() - 1) + u;
            } else {
                return baseUrl + u;
            }
        }
        return u;
    }

    private String getBaseUrl(HttpRequestBase request) {
        return request.getURI().getScheme() + "://" + request.getURI().getHost();
    }

    private boolean isList(Field htmlField) {
        return htmlField.getType().isAssignableFrom(List.class);
    }

    private Object getElementAttrOrText(Html htmlAnno, Element element) {
        Object value;
        if (StringUtils.isEmpty(htmlAnno.attr())) {
            value = element.text();
        } else {
            value = element.attr(htmlAnno.attr());
        }
        return value;
    }
}
