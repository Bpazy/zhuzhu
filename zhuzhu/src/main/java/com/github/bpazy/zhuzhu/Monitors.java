package com.github.bpazy.zhuzhu;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author ziyuan
 * created on 2019/10/9
 */
public class Monitors {
    private static final List<Runnable> activeTasks = Collections.synchronizedList(new ArrayList<>());

    public static Runnable getRunnable(Runnable task) {
        activeTasks.add(task);
        return () -> {
            try {
                task.run();
            } finally {
                activeTasks.remove(task);
            }
        };
    }

    public static int activeSize() {
        return activeTasks.size();
    }
}
