package com.github.bpazy.zhuzhu.pipeline;

import lombok.extern.slf4j.Slf4j;

/**
 * @author ziyuan
 */
@Slf4j
public class ConsolePipeline implements Pipeline<Object> {
    @Override
    public void process(Object o) {
        log.info("{}", o);
    }
}
