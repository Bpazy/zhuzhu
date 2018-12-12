package com.github.bpazy.zhuzhu.pipeline;

import com.github.bpazy.zhuzhu.annotation.Bean;
import com.google.common.cache.CacheLoader;
import lombok.SneakyThrows;
import org.apache.commons.lang3.ClassUtils;

/**
 * @author ziyuan
 */
public class Pipelines {
    private static final CacheLoader<String, Pipeline> cacheLoader = new CacheLoader<String, Pipeline>() {
        @Override
        public Pipeline load(String clazz) throws Exception {
            return (Pipeline) ClassUtils.getClass(clazz).newInstance();
        }
    };

    @SneakyThrows
    public static void run(Bean bean, Object object) {
        for (Class pipeline : bean.pipelines()) {
            cacheLoader.load(pipeline.getName()).process(object);
        }
    }
}
