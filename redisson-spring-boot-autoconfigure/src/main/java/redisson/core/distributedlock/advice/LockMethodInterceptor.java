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
package cn.xphsc.redisson.core.distributedlock.advice;

import cn.xphsc.redisson.annotation.RedissonLock;
import cn.xphsc.redisson.core.distributedlock.LockInfoProvider;
import cn.xphsc.redisson.core.distributedlock.entity.LockBuilder;
import cn.xphsc.redisson.core.distributedlock.entity.LockInfo;
import cn.xphsc.redisson.core.distributedlock.lock.Lock;
import cn.xphsc.redisson.core.distributedlock.lock.RedissonLockFactory;
import cn.xphsc.redisson.core.distributedlock.handler.RedissonLockInvocationException;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.context.ApplicationContext;
import org.springframework.util.StringUtils;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * {@link MethodInterceptor}
 * @author <a href="xiongpeih@163.com">huipei.x</a>
 * @description:
 * @since 1.0.0
 */
public class LockMethodInterceptor implements MethodInterceptor{
    private RedissonLockFactory lockFactory;
    private LockInfoProvider lockInfoProvider;

public LockMethodInterceptor(ApplicationContext context){
    this.lockFactory=context.getBean(RedissonLockFactory.class);
    this.lockInfoProvider=context.getBean(LockInfoProvider.class);
}


    private ThreadLocal<Lock> currentThreadLock = new ThreadLocal<Lock>();
    private ThreadLocal<LockBuilder> currentThreadLockRes = new ThreadLocal<LockBuilder>();
    @Override
    public Object invoke(MethodInvocation methodInvocation) throws Throwable {
        RedissonLock redissonLock =methodInvocation.getMethod().getAnnotation(RedissonLock.class);
        LockInfo lockInfo = lockInfoProvider.get(methodInvocation.getMethod(),methodInvocation.getThis(),methodInvocation.getArguments(), redissonLock);
        currentThreadLockRes.set(new LockBuilder(lockInfo, false));
        Lock lock = lockFactory.getLock(lockInfo);
        boolean lockRes = lock.acquire();

        if (!lockRes) {
            if (!StringUtils.isEmpty(redissonLock.customLockTimeoutStrategy())) {
                return handleCustomLockTimeout(redissonLock.customLockTimeoutStrategy(), methodInvocation);
            } else {
                redissonLock.lockTimeoutStrategy().handle(lockInfo, lock);
                return null;
            }
        }

        currentThreadLock.set(lock);
        currentThreadLockRes.get().setRes(true);
        Object  result;
        try {
              result=methodInvocation.proceed();
        }finally {
            releaseLock(redissonLock,methodInvocation);
            clean();
        }

        return result;
    }

    /**
     * 处理自定义加锁超时
     */
    private Object handleCustomLockTimeout(String lockTimeoutHandler,MethodInvocation methodInvocation) throws Throwable {

        // prepare invocation context
        Method currentMethod = methodInvocation.getMethod();
        Object target = methodInvocation.getThis();
        Method handleMethod = null;
        try {
            handleMethod = methodInvocation.getThis().getClass().getDeclaredMethod(lockTimeoutHandler, currentMethod.getParameterTypes());
            handleMethod.setAccessible(true);
        } catch (NoSuchMethodException e) {
            throw new IllegalArgumentException("Illegal annotation param customLockTimeoutStrategy", e);
        }
        Object[] args = methodInvocation.getArguments();
        Object res = null;
        try {
            res = handleMethod.invoke(target, args);
        } catch (IllegalAccessException e) {
            throw new RedissonLockInvocationException("Fail to invoke custom lock timeout handler: " + lockTimeoutHandler, e);
        } catch (InvocationTargetException e) {
            throw e.getTargetException();
        }

        return res;
    }

    /**
     * 释放锁
     */
    private void releaseLock(RedissonLock redissonLock, MethodInvocation methodInvocation) throws Throwable {
        LockBuilder lockRes = currentThreadLockRes.get();
        if (lockRes.getRes()) {
            boolean releaseRes = currentThreadLock.get().release();
            // avoid release lock twice when exception happens below
            lockRes.setRes(false);
            if (!releaseRes) {
                handleReleaseTimeout(redissonLock, lockRes.getLockInfo(), methodInvocation);
            }
        }
    }




    /**
     * 处理释放锁时已超时
     */
    private void handleReleaseTimeout(RedissonLock redissonLock, LockInfo lockInfo,MethodInvocation methodInvocation) throws Throwable {
        if (!StringUtils.isEmpty(redissonLock.customReleaseTimeoutStrategy())) {

            handleCustomReleaseTimeout(redissonLock.customReleaseTimeoutStrategy(), methodInvocation);

        } else {
            redissonLock.releaseTimeoutStrategy().handle(lockInfo);
        }

    }
    private void handleCustomReleaseTimeout(String releaseTimeoutHandler, MethodInvocation methodInvocation) throws Throwable {

        Method currentMethod = methodInvocation.getMethod();
        Object obj = methodInvocation.getThis();
        Method handleMethod = null;
        try {
            handleMethod =  methodInvocation.getThis().getClass().getDeclaredMethod(releaseTimeoutHandler, currentMethod.getParameterTypes());
            handleMethod.setAccessible(true);
        } catch (NoSuchMethodException e) {
            throw new IllegalArgumentException("Illegal annotation param customReleaseTimeoutStrategy", e);
        }
        Object[] args = methodInvocation.getArguments();

        try {
            handleMethod.invoke(obj, args);
        } catch (IllegalAccessException e) {
            throw new RedissonLockInvocationException("Fail to invoke custom release timeout handler: " + releaseTimeoutHandler, e);
        } catch (InvocationTargetException e) {
            throw e.getTargetException();
        }
    }

    private void clean() {

        currentThreadLockRes.remove();
        currentThreadLock.remove();
    }
}
