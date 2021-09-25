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
package cn.xphsc.redisson.core.distributedlock;


import cn.xphsc.redisson.annotation.RedissonLock;
import cn.xphsc.redisson.boot.RedissonLockProperties;
import cn.xphsc.redisson.core.distributedlock.entity.LockInfo;
import cn.xphsc.redisson.core.distributedlock.entity.LockType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;


/**
 * {@link }
 * @author <a href="xiongpeih@163.com">huipei.x</a>
 * @description:
 * @since 1.0.0
 */
public class LockInfoProvider {
    private RedissonLockProperties redissonLockProperties;
    private RedissonLockKeyProvider businessKeyProvider;
    public static final String LOCK_NAME_SEPARATOR = ":lock:";
    public LockInfoProvider(){

    }
    public LockInfoProvider(RedissonLockProperties redissonLockProperties,
                            RedissonLockKeyProvider businessKeyProvider){
        this.businessKeyProvider=businessKeyProvider;
        this.redissonLockProperties=redissonLockProperties;
    }

    /**
     * 默认锁的前缀
     */
    @Value("${spring.application.name:redisson-lock}")
    private String defaultLockPrefix;

    private static final Logger logger = LoggerFactory.getLogger(LockInfoProvider.class);

    public LockInfo get(Method method, Object target, Object[] args, RedissonLock redissonLock) {
        LockType type = redissonLock.lockType();
        String businessKeyName = businessKeyProvider.getKeyName(method,target,args, redissonLock);
        String lockPrefix = StringUtils.hasText(redissonLockProperties.getLockPrefix()) ? redissonLockProperties.getLockPrefix() : defaultLockPrefix;
        String lockName = lockPrefix + LOCK_NAME_SEPARATOR + redissonLock.name() + businessKeyName;
        long waitTime = getWaitTime(redissonLock);
        long leaseTime = getLeaseTime(redissonLock);

        if (leaseTime == -1 && logger.isWarnEnabled()) {
            logger.warn("Trying to acquire Lock({}) with no expiration, " +
                "redissonLock will keep prolong the lock expiration while the lock is still holding by current thread. " +
                "This may cause dead lock in some circumstances.", lockName);
        }

        return new LockInfo(type, lockName, waitTime, leaseTime);
    }




    private long getWaitTime(RedissonLock lock) {
        return lock.waitTime() == Long.MIN_VALUE ?
                redissonLockProperties.getWaitTime() : lock.waitTime();
    }

    private long getLeaseTime(RedissonLock lock) {
        return lock.leaseTime() == Long.MIN_VALUE ?
                redissonLockProperties.getLeaseTime() : lock.leaseTime();
    }
}
