package com.github.bpazy.zhuzhu.schdule;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author ziyuan
 */
public class UniqueSchedule implements Schedule {
    private Lock lock = new ReentrantLock();

    private List<String> seeds = Lists.newArrayList();
    private Set<String> visited = Sets.newHashSet();

    @Override
    public String take() {
        try {
            lock.lock();
            if (CollectionUtils.isEmpty(seeds)) return "";

            return seeds.remove(0);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void add(String url) {
        try {
            lock.lock();

            boolean notVisited = visited.add(url);
            if (notVisited) {
                seeds.add(url);
            }
        } finally {
            lock.unlock();
        }
    }

    @Override
    public long size() {
        return seeds.size();
    }
}
