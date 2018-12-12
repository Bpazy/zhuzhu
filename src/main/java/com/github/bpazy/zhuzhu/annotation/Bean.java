package com.github.bpazy.zhuzhu.annotation;

import java.lang.annotation.*;

/**
 * @author ziyuan
 */
@Inherited
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Bean {
    String match() default "*";

    Class[] pipelines();
}
