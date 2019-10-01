package com.github.bpazy.zhuzhu;

/**
 * @author ziyuan
 */
public interface WebCrawler {
    boolean shouldVisit(String url);

    // TODO Maybe object wrapper is needed because of multiple parameters.
    void visit(String url, byte[] content);
}
