package com.github.bpazy.zhuzhu;

/**
 * @author ziyuan
 */
public interface WebCrawler {
    boolean shouldVisit(String url);


    /**
     * There should be no operations that are blocked for too long because the crawler thread pool is used
     * TODO Maybe object wrapper is needed because of multiple parameters.
     *
     * @param url
     * @param content
     */
    void visit(String url, byte[] content);

    // TODO Maybe pipeline is better.
    //  shouldVisit -> visit(filter) -> handle(filtered data)
}
