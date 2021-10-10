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
package cn.xphsc.redisson.core.ratelimiter;


import cn.xphsc.redisson.annotation.RateLimit;
import cn.xphsc.redisson.core.ratelimiter.entity.DurationStyle;
import cn.xphsc.redisson.core.ratelimiter.exception.ExecuteFunctionException;
import cn.xphsc.redisson.core.ratelimiter.entity.RateLimiterInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
/**
 * {@link }
 * @author <a href="xiongpeih@163.com">huipei.x</a>
 * @description:
 * @since 1.0.0
 */
public class RateLimiterHander {

    private static final String NAME_PREFIX = "RateLimiter_";
    private static final Logger logger = LoggerFactory.getLogger(RateLimiterHander.class);
    private RatelimitProvider ratelimitProvider;
    public RateLimiterHander(RatelimitProvider ratelimitProvider){
        this.ratelimitProvider=ratelimitProvider;
    }

    public RateLimiterInfo getRateLimiterInfo(Method method, Object target, Object[] args, RateLimit rateLimit) {
        String businessKeyName = ratelimitProvider.getKeyName(method,args,target, rateLimit);
        String rateLimitKey = getName(method) + businessKeyName;
        if (StringUtils.hasLength(rateLimit.customKey())) {
            try {
                rateLimitKey = getName(method) + this.executeFunction(rateLimit.customKey(), method,target,args).toString();
            } catch (Throwable throwable) {
                logger.info("Gets the custom Key exception and degrades it to the default Key:{}", rateLimit, throwable);
            }
        }
        long rate = ratelimitProvider.getRateValue(rateLimit);
        long rateInterval = DurationStyle.detectAndParse(rateLimit.rateInterval()).getSeconds();
        return new RateLimiterInfo(rateLimitKey, rate, rateInterval);
    }





    /**
     * 执行自定义函数
     */
    public Object executeFunction(String fallbackName, Method method, Object target, Object[] args) throws Throwable {
        Method handleMethod = null;
        try {
            handleMethod =target.getClass().getDeclaredMethod(fallbackName, method.getParameterTypes());
            handleMethod.setAccessible(true);
        } catch (NoSuchMethodException e) {
            throw new IllegalArgumentException("Illegal annotation param customLockTimeoutStrategy", e);
        }
        // invoke
        Object result = null;
        try {
            result = handleMethod.invoke(target, args);
        } catch (IllegalAccessException e) {
            throw new ExecuteFunctionException("Fail to invoke custom lock timeout handler: " + fallbackName, e);
        } catch (InvocationTargetException e) {
            throw e.getTargetException();
        }

        return result;
    }


    /**
     * 获取基础的限流 key
     */
    private String getName(Method  method) {
        return NAME_PREFIX + String.format("%s.%s", method.getDeclaringClass(),method.getName());

    }
}
