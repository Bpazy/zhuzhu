package com.github.bpazy.zhuzhu.http;

import java.io.IOException;

/**
 * @author ziyuan
 * created on 2019/10/8
 */
public interface HttpClient {
    byte[] get(String url, RequestConfig requestConfig) throws IOException;
}
