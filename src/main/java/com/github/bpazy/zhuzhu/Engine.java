package com.github.bpazy.zhuzhu;

import com.github.bpazy.zhuzhu.spider.Spider;
import com.google.common.collect.Lists;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author ziyuan
 */
public class Engine {
    private EngineOption option;
    private List<Class> beans;

    public Engine(EngineOption option) {
        this.option = option;
        beans = Lists.newArrayList();
    }

    public Engine bean(Class clazz) {
        beans.add(clazz);
        return this;
    }

    public void start() {
        ExecutorService executor = Executors.newFixedThreadPool(option.getThreadNum());
        executor.submit(new Spider(option, beans));
    }
}
