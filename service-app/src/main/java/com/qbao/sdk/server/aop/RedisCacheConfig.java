package com.qbao.sdk.server.aop;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * redis缓存
 * @author xuefeihu
 */
@Target({ ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
public @interface RedisCacheConfig {
    String key() default "store";

    String fieldKey() default "";

    int expireTime() default 1800;
}
