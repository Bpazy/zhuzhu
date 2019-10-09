package com.github.bpazy.zhuzhu.http;

/**
 * @author ziyuan
 * created on 2019/10/8
 */
public class BasicHeader implements Header {
    private String name;
    private String value;

    public BasicHeader(String name, String value) {
        this.name = name;
        this.value = value;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getValue() {
        return value;
    }
}
