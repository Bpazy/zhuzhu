package com.github.bpazy.zhuzhu.http;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.List;

/**
 * @author ziyuan
 * created on 2019/10/8
 */
public class DefaultHttpClient implements HttpClient {
    private CloseableHttpClient client = HttpClients.createDefault();

    public DefaultHttpClient() {
    }

    @Override
    public byte[] get(String url, RequestConfig requestConfig) throws IOException {
        HttpGet httpGet = new HttpGet(url);
        List<Header> headers = requestConfig.getHeaders();
        if (CollectionUtils.isNotEmpty(headers)) {
            headers.stream()
                    .map(header -> new BasicHeader(header.getName(), header.getValue()))
                    .forEach(httpGet::addHeader);
        }

        org.apache.http.client.config.RequestConfig.Builder builder =
                org.apache.http.client.config.RequestConfig.custom()
                        .setCookieSpec(CookieSpecs.STANDARD)
                        .setConnectTimeout(requestConfig.getTimeout())
                        .setConnectionRequestTimeout(requestConfig.getTimeout())
                        .setSocketTimeout(requestConfig.getTimeout());
        HttpHost proxy = requestConfig.getProxy();
        if (proxy != null) {
            builder.setProxy(new org.apache.http.HttpHost(proxy.getHost(), proxy.getPort()));
        }

        httpGet.setConfig(builder.build());
        return EntityUtils.toByteArray(client.execute(httpGet).getEntity());
    }
}
