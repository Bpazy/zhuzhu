package com.github.bpazy.zhuzhu;

import com.github.bpazy.zhuzhu.annotation.Bean;
import com.github.bpazy.zhuzhu.annotation.Html;
import com.github.bpazy.zhuzhu.annotation.Title;
import com.github.bpazy.zhuzhu.annotation.Url;
import com.github.bpazy.zhuzhu.pipeline.ConsolePipeline;
import lombok.Data;

import java.util.List;

/**
 * @author ziyuan
 */
@Data
@Bean(pipelines = ConsolePipeline.class, match = "https://github.com/Bpazy")
public class Github {

    @Title
    private String title;

    @Url
    private String url;

    @Html(selector = "p")
    private List<String> p;
}
