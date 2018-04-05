package com.qbao.sdk.server.aop;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 删除redis缓存
 * @author xuefeihu
 *
 */
@Target({ ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
public @interface RedisCacheDelConfig {

    String[] key() default "SdkServer";

    String[] fieldKey() default {};

}
