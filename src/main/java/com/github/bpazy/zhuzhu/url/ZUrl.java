package com.github.bpazy.zhuzhu.url;

import com.github.bpazy.zhuzhu.Util;
import lombok.SneakyThrows;

public class ZUrl {
    private String url;

    @SneakyThrows
    public static ZUrl normalize(String url) {
        ZUrl zUrl = new ZUrl();
        zUrl.url = Util.normalizeUrl(url);
        return zUrl;
    }

    public String getUrl() {
        return url;
    }

    public String getDomain() {
        return Util.getDomain(url);
    }

    /* =============== private method area =============== */
}
