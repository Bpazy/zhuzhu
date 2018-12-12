package com.github.bpazy.zhuzhu;

import com.github.bpazy.zhuzhu.schdule.Schedule;
import lombok.Builder;
import lombok.Data;

/**
 * @author ziyuan
 */
@Data
@Builder
public class EngineOption {
    private int threadNum;
    private String charset;
    private String startUrl;
    private Schedule schedule;
}
