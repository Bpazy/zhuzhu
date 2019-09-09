package com.github.bpazy.zhuzhu;

import com.github.bpazy.zhuzhu.schdule.Schedule;
import com.github.bpazy.zhuzhu.schdule.UniqueSchedule;
import org.joor.Reflect;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author ziyuan
 * created on 2019/9/2
 */
public class CrawlerControllerTest {
    private CrawlerController controller;

    @BeforeEach
    public void init() {
        controller = new CrawlerController();
    }

    @Test
    public void test() {
        controller.addSeed("https://github.com");
        assertThat(Reflect.on(controller).<Schedule>get("schedule")).isInstanceOf(UniqueSchedule.class);

        Schedule schedule = new TestSchedule();
        controller.setSchedule(schedule);
        controller.addSeed("https://github.com");
        assertThat(Reflect.on(controller).<Schedule>get("schedule")).isInstanceOf(TestSchedule.class);
    }

    @Test
    public void testThreadNum() {
        controller.setThreadNum(-1);
        controller.setTimeout(-1);
        Reflect.on(controller).call("init");
        assertThat(Reflect.on(controller).<Schedule>get("threadNum")).isEqualTo(1);
        assertThat(Reflect.on(controller).<Schedule>get("timeout")).isEqualTo(3000);
        controller.setThreadNum(Integer.MAX_VALUE);
        assertThat(Reflect.on(controller).<Schedule>get("threadNum")).isEqualTo(Integer.MAX_VALUE);
    }

    private static class TestSchedule implements Schedule {
        @Override
        public String take() {
            return null;
        }

        @Override
        public void add(String url) {

        }
    }
}