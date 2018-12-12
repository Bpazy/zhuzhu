package com.github.bpazy.zhuzhu.annotation;

import java.lang.annotation.*;

/**
 * @author ziyuan
 */
@Inherited
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Html {
    String selector();

    String attr() default "";
}
