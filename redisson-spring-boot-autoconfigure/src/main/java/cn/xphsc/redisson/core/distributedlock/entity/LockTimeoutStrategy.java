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
package cn.xphsc.redisson.core.distributedlock.entity;


import cn.xphsc.redisson.core.distributedlock.handler.RedissonLockTimeoutException;
import cn.xphsc.redisson.core.distributedlock.handler.lock.LockTimeoutHandler;
import cn.xphsc.redisson.core.distributedlock.lock.Lock;

import java.util.concurrent.TimeUnit;


/**
 * {@link }
 * @author <a href="xiongpeih@163.com">huipei.x</a>
 * @description:
 * @since 1.0.0
 */
public enum LockTimeoutStrategy implements LockTimeoutHandler {

    /**
     * 继续执行业务逻辑，不做任何处理
     */
    NO_OPERATION() {
        @Override
        public void handle(LockInfo lockInfo, Lock lock) {
        }


    },

    /**
     * 快速失败
     */
    FAIL_FAST() {


        @Override
        public void handle(LockInfo lockInfo, Lock lock) {
            String errorMsg = String.format("Failed to acquire Lock(%s) with timeout(%ds)", lockInfo.getName(), lockInfo.getWaitTime());
            throw new RedissonLockTimeoutException(errorMsg);
        }
    },

    /**
     * 一直阻塞，直到获得锁，在太多的尝试后，仍会报错
     */
    KEEP_ACQUIRE() {

        private static final long DEFAULT_INTERVAL = 100L;

        private static final long DEFAULT_MAX_INTERVAL = 3 * 60 * 1000L;

        @Override
        public void handle(LockInfo lockInfo, Lock lock) {

            long interval = DEFAULT_INTERVAL;

            while(!lock.acquire()) {

                if(interval > DEFAULT_MAX_INTERVAL) {
                    String errorMsg = String.format("Failed to acquire Lock(%s) after too many times, this may because dead lock occurs.",
                                                     lockInfo.getName());
                    throw new RedissonLockTimeoutException(errorMsg);
                }

                try {
                    TimeUnit.MILLISECONDS.sleep(interval);
                    interval <<= 1;
                } catch (InterruptedException e) {
                    throw new RedissonLockTimeoutException("Failed to acquire Lock", e);
                }
            }
        }


    }
}