package com.github.bpazy.zhuzhu.schdule;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class RedisUniqueScheduleTest {
    private final String testUrl = "https://github.com";

    private RedisUniqueSchedule schedule;

    @BeforeEach
    public void init() {
        schedule = new RedisUniqueSchedule("127.0.0.1", 6379, true) {
            @Override
            protected String getKey(String url) {
                return "zhuzhu:test:" + url;
            }

            @Override
            protected String getListKey() {
                return "zhuzhu:test:list";
            }
        };
    }

    @AfterEach
    public void destroy() {
        schedule.close();
    }

    @Test
    void test() {
        schedule.add(testUrl);
        assertThat(schedule.take()).isEqualTo(testUrl);
        schedule.markHandled(testUrl);
        assertThat(schedule.size()).isEqualTo(0);
    }
}