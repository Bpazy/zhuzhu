package com.github.bpazy.zhuzhu.schdule;

/**
 * @author ziyuan
 */
public interface Schedule {
    /**
     * get a url from schedule
     *
     * @return url
     */
    String take();

    /**
     * add a url to schedule
     *
     * @param url the url you want add to schedule
     */
    void add(String url);
}
