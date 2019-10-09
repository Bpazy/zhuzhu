package com.github.bpazy.zhuzhu.http;

/**
 * @author ziyuan
 * created on 2019/10/8
 */
public class HttpHost {
    private String host;
    private int port;

    public HttpHost(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }
}
