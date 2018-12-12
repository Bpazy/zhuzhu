package com.github.bpazy.zhuzhu.schdule;

import com.google.common.collect.Lists;
import lombok.SneakyThrows;

import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * @author ziyuan
 */
public class UniqueSchedule implements Schedule {
    private ReadWriteLock lock = new ReentrantReadWriteLock();
    private int index = 0;
    private List<String> urls = Lists.newArrayList();

    private Lock waitLock = new ReentrantLock();

    @Override
    @SneakyThrows
    public void in(String url) {
        try {
            lock.writeLock().lock();
            if (urls.contains(url)) return;
            urls.add(url);
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public String out() {
        if (index >= urls.size()) return null;

        String url;
        try {
            lock.readLock().lock();
            url = urls.get(index);
        } finally {
            lock.readLock().unlock();
        }
        index++;
        return url;
    }
}
