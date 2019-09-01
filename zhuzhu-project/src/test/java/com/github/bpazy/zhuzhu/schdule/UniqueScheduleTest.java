package com.github.bpazy.zhuzhu.schdule;

import com.google.common.collect.Lists;
import org.joor.Reflect;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.concurrent.CountDownLatch;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author ziyuan
 * created on 2019/9/1
 */
public class UniqueScheduleTest {
    private Schedule schedule;

    @Before
    public void init() {
        schedule = new UniqueSchedule();
    }

    @Test
    public void oneThread() {
        String seed = "https://github.com/Bpazy";
        schedule.add(seed);
        List<String> seeds = Reflect.on(schedule).get("seeds");
        assertThat(seeds).hasSize(1);
        assertThat(schedule.take()).isEqualTo(seed);
        assertThat(seeds).hasSize(0);
    }

    @Test
    public void multiThreads() throws InterruptedException {
        int threadNum = 100;

        CountDownLatch latch = new CountDownLatch(threadNum);
        for (int i = 0; i < threadNum; i++) {
            int finalI = i;
            new Thread(() -> {
                schedule.add("https://github.com/Bpazy/" + finalI);
                latch.countDown();
            }).start();
        }
        latch.await();

        List<String> expectedUrls = Lists.newArrayListWithExpectedSize(100);
        for (int i = 0; i < threadNum; i++) {
            expectedUrls.add("https://github.com/Bpazy/" + i);
        }
        List<String> seeds = Reflect.on(schedule).get("seeds");
        assertThat(seeds).hasSize(100);
        for (int i = 0; i < threadNum; i++) {
            assertThat(expectedUrls).contains(schedule.take());
        }
        assertThat(seeds).hasSize(0);
    }
}