package com.qbao.sdk.server.aop;

import java.lang.reflect.Method;

import javax.annotation.Resource;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.alibaba.fastjson.JSONObject;
import com.qbao.sdk.server.util.RedisUtil;

/**
 * redis缓存处理AOP
 * @author xuefeihu
 *
 */
@Component
@Aspect
public class RedisCacheAspectHandler {

    private static final Logger LOG = LoggerFactory.getLogger(RedisCacheAspectHandler.class);

    /** redis操作类 **/
    @Resource
    private RedisUtil redisUtil;

    // 定义切入点
    @Pointcut("@annotation(com.qbao.store.util.RedisCacheConfig)")
    public void cachePointcut() {
    }

    @Pointcut("@annotation(com.qbao.store.util.RedisCacheDelConfig)")
    public void delPointcut() {
    }

    /**
     * 清除缓存
     * @param joinPoint
     */
    @After("delPointcut()")
    public void delCache(JoinPoint joinPoint) {
        Signature signature = joinPoint.getSignature();
        MethodSignature methodSignature = (MethodSignature) signature; // 获取方法签名
        Method method = methodSignature.getMethod();

        RedisCacheDelConfig delConfig = method.getAnnotation(RedisCacheDelConfig.class);
        String[] hashKey = delConfig.key();
        String[] fieldKey = delConfig.fieldKey();

        if ((null != fieldKey) && (0 != fieldKey.length)) { // 若提供指定hash的fieldkey，则删除指定的fieldkey，否则直接删除hash
            this.redisUtil.hdel(hashKey[0], fieldKey);
        } else {
            this.redisUtil.del(hashKey);
        }

    }

    /**
     * 将方法返回值放入切点
     * @param joinPoint
     * @return
     */
    @Around("cachePointcut()")
    public Object putCache(ProceedingJoinPoint joinPoint) {

        Object result = null;

        Signature signature = joinPoint.getSignature();
        MethodSignature methodSignature = (MethodSignature) signature; // 获取方法签名
        Method method = methodSignature.getMethod();

        RedisCacheConfig cacheable = method.getAnnotation(RedisCacheConfig.class);
        String fieldKey = cacheable.fieldKey();
        String hashKey = cacheable.key();
        if (StringUtils.isEmpty(fieldKey)) { // 若无指定的field 方法参数.toString()
            fieldKey = this.getRedisKey(joinPoint);
        }
        result = this.redisUtil.hget(hashKey, fieldKey, method.getGenericReturnType());
        if (null == result) {
            try {
                result = joinPoint.proceed(); // 获取方法返回值
                if (null != result) {
                    LOG.info("result:{}, json:{}", result, JSONObject.toJSONString(result));
                    String value = JSONObject.toJSONString(result);
                    if (!StringUtils.isEmpty(value) && !"{}".equals(value) && !"[]".equals(value)) {
                        this.redisUtil.hset(hashKey, fieldKey, JSONObject.toJSONString(result), cacheable.expireTime());
                    }
                }
            } catch (Throwable throwable) {
                LOG.error("RedisCacheAspectHandler.putCache key:{},fieldKey:{},value:{},exception:{}", 
                		hashKey, fieldKey, result, throwable);
            }
        }
        return result;
    }

    /**
     * 获取redis key值 方法名 + 方法参数.toString()
     * @param joinPoint
     * @param method
     * @return
     */
    private String getRedisKey(ProceedingJoinPoint joinPoint) {
        Object[] paramArgs = joinPoint.getArgs();
        StringBuffer keyName = new StringBuffer();
        // redis 方法参数.toString()  value = 返回结果的json字符串
        if ((null != paramArgs) && (paramArgs.length != 0)) {
            for (Object object : paramArgs) {
                if ((null != object) && !StringUtils.isEmpty(object.toString())) {
                    keyName.append(":").append(object.toString());
                }
            }
        }
        String result = keyName.toString();
        if (!StringUtils.isEmpty(result)) {
            return result.substring(1);
        }
        return null;
    }
}
