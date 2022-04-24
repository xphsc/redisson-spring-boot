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



import cn.xphsc.redisson.core.distributedlock.entity.LockTimeoutStrategy;
import cn.xphsc.redisson.core.distributedlock.entity.LockType;
import cn.xphsc.redisson.core.distributedlock.entity.ReleaseTimeoutStrategy;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * {@link }
 * @author <a href="xiongpeih@163.com">huipei.x</a>
 * @description: 锁注解
 * For reference, the example
 * @RedissonLock(name = "test-lock", keys = {"#id"}, leaseTime=-1)
 * public void testLock(String id){}
 * @see  RedissonLockKey
 * @since 1.0.0
 */
@Target(value = {ElementType.METHOD})
@Retention(value = RetentionPolicy.RUNTIME)
public @interface RedissonLock {
    /**
     * 锁的名称
     */
    String name();

    /**
     * 锁类型，默认可重入锁
     */
    LockType lockType() default LockType.Reentrant;

    /**
     * 尝试加锁，最多等待时间
     */
    long waitTime() default Long.MIN_VALUE;

    /**
     * 上锁自动解锁
     */
    long leaseTime() default Long.MIN_VALUE;

    /**
     * 自定义业务key
     */
    String[] keys() default {};

    /**
     * 加锁超时的处理策略
     */
    LockTimeoutStrategy lockTimeoutStrategy() default LockTimeoutStrategy.NO_OPERATION;

    /**
     * 自定义加锁超时的处理策略
     */
    String customLockTimeoutStrategy() default "";

    /**
     * 释放锁时已超时的处理策略
     */
    ReleaseTimeoutStrategy releaseTimeoutStrategy() default ReleaseTimeoutStrategy.NO_OPERATION;

    /**
     * 自定义释放锁时已超时的处理策略
     */
    String customReleaseTimeoutStrategy() default "";

}
