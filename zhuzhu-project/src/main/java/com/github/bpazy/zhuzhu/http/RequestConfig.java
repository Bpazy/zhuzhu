package com.github.bpazy.zhuzhu.http;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * @author ziyuan
 * created on 2019/10/8
 */
@Getter
public class RequestConfig {
    private int timeout;
    private HttpHost proxy;

    private List<Header> headers;

    public static Builder builder() {
        return new Builder();
    }

    @Slf4j
    public static class Builder {
        private static final int REQUEST_TIMEOUT = 3000;

        private int timeout;
        private HttpHost proxy;

        private List<Header> headers;

        public Builder setTimeout(int timeout) {
            this.timeout = timeout;
            return this;
        }

        public Builder setProxy(HttpHost proxy) {
            this.proxy = proxy;
            return this;
        }

        public Builder setHeaders(List<Header> headers) {
            this.headers = headers;
            return this;
        }

        public RequestConfig build() {
            RequestConfig config = new RequestConfig();
            if (timeout <= 0) {
                log.warn("timeout should be greater than 0. The timeout is now set to {}ms.", REQUEST_TIMEOUT);
                timeout = REQUEST_TIMEOUT;
            }
            config.headers = headers;
            config.proxy = proxy;
            config.timeout = timeout;
            return config;
        }
    }
}
