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
package cn.xphsc.redisson.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * {@link }
 * @author <a href="xiongpeih@163.com">huipei.x</a>
 * @description: Current limiting rules
 * For reference, the example
 * @RateLimit(rate = 1, rateInterval = "10s")
 *  public String get() {}
  @RateLimit(rate = 5, rateInterval = "10s",keys = {"#user.name","#user.id"})
 *  public String hello(User user) { }
 @see RateLimitKey
 * @since 1.0.0
 */
@Target(value = {ElementType.METHOD})
@Retention(value = RetentionPolicy.RUNTIME)
public @interface RateLimit {

    /**
     * 时间窗口流量数量
     */
    long rate();

    /**
     * 时间窗口流量数量表达式
     */
    String rateExpression() default "";

    /**
     * 时间窗口，最小单位秒，如 2s，2h , 2d
     */
    String rateInterval();

    /**
     * 获取key
     */
    String [] keys() default {};

    /**
     * 限流后的回退后的拒绝逻辑
     */
    String fallback() default "";

    /**
     * 自定义key
     */
    String customKey() default "";

}
