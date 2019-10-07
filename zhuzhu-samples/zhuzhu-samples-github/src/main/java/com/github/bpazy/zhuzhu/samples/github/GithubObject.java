package com.github.bpazy.zhuzhu.samples.github;

import lombok.Builder;
import lombok.Data;

/**
 * @author ziyuan
 * created on 2019/10/7
 */
@Data
@Builder
public class GithubObject {
    private String url;
    private String repo;
    private String star;
}
