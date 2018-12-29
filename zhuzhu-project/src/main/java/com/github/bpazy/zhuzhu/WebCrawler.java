package com.github.bpazy.zhuzhu;

/**
 * @author ziyuan
 */
public interface WebCrawler {
    boolean shouldVisit(String url);

    void visit(String url, byte[] content);
}
