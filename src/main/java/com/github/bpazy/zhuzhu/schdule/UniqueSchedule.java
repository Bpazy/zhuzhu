package com.github.bpazy.zhuzhu.schdule;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import java.util.List;
import java.util.Set;

/**
 * @author ziyuan
 */
public class UniqueSchedule implements Schedule {
    private List<String> seeds = Lists.newArrayList(); // TODO thread safe
    private Set<String> visited = Sets.newHashSet();   // TODO thread safe

    @Override
    public boolean hasMore() {
        return seeds.size() > 0;
    }

    @Override
    public String take() {
        return seeds.remove(0);
    }

    @Override
    public boolean unVisited(String url) {
        return visited.add(url);
    }

    @Override
    public void add(String url) {
        seeds.add(url);
    }
}
