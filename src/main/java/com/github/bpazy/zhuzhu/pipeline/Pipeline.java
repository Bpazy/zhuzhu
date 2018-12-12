package com.github.bpazy.zhuzhu.pipeline;

/**
 * @author ziyuan
 */
public interface Pipeline<T> {
    void process(T t);
}
