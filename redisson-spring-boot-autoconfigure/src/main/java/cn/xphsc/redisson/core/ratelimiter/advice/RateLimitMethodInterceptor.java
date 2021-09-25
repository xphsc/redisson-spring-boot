/*
 * Copyright (c) 2021 huipei.x
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cn.xphsc.redisson.core.ratelimiter.advice;

import cn.xphsc.redisson.annotation.RateLimit;
import cn.xphsc.redisson.utils.AnnotationScanUtils;
import cn.xphsc.redisson.core.ratelimiter.RateLimiterHander;
import cn.xphsc.redisson.core.ratelimiter.entity.LuaScript;
import cn.xphsc.redisson.core.ratelimiter.entity.RateLimiterInfo;
import cn.xphsc.redisson.core.ratelimiter.exception.RateLimitException;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.redisson.api.RScript;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * {@link MethodInterceptor}
 * @author <a href="xiongpeih@163.com">huipei.x</a>
 * @description:
 * @since 1.0.0
 */
public class RateLimitMethodInterceptor implements MethodInterceptor {
    private static final Logger logger = LoggerFactory.getLogger(RateLimitMethodInterceptor.class);

    private final RateLimiterHander rateLimiterHander;
    private final RScript rScript;
    public RateLimitMethodInterceptor(ApplicationContext context) {
        this.rateLimiterHander = context.getBean(RateLimiterHander.class);
        this.rScript = context.getBean(RedissonClient.class).getScript();
    }

    @Override
    public Object invoke(MethodInvocation methodInvocation) throws Throwable {
        RateLimit rateLimit= AnnotationScanUtils.findAnnotation(methodInvocation.getMethod(), RateLimit.class);
        RateLimiterInfo limiterInfo = rateLimiterHander.getRateLimiterInfo(methodInvocation.getMethod(),methodInvocation.getThis(),methodInvocation.getArguments(), rateLimit);
        List<Object> keys = new ArrayList<>();
        keys.add(limiterInfo.getKey());
        keys.add(limiterInfo.getRate());
        keys.add(limiterInfo.getRateInterval());
        List<Long> results = rScript.eval(RScript.Mode.READ_WRITE, LuaScript.getRateLimiterScript(), RScript.ReturnType.MULTI, keys);
        boolean allowed = results.get(0) == 0L;
        if (!allowed) {
            logger.info("Trigger current limiting,key:{}", limiterInfo.getKey());
            if (StringUtils.hasLength(rateLimit.fallbackFunction())) {
                return rateLimiterHander.executeFunction(rateLimit.fallbackFunction(), methodInvocation.getMethod(),methodInvocation.getThis(),methodInvocation.getArguments());
            }
            long ttl = results.get(1);
            throw new RateLimitException("Too Many Requests", ttl);
        }
        return methodInvocation.proceed();
    }
}
