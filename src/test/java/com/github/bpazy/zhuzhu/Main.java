package com.github.bpazy.zhuzhu;

import com.github.bpazy.zhuzhu.schdule.UniqueSchedule;
import lombok.extern.slf4j.Slf4j;

/**
 * @author ziyuan
 */
@Slf4j
public class Main {
    public static void main(String[] args) {
        EngineOption option = new EngineOption(1, "UTF8", "https://github.com/Bpazy", new UniqueSchedule());
        log.debug("option: {}", option);
        new Engine(option)
                .bean(Github.class)
                .start();
    }
}

