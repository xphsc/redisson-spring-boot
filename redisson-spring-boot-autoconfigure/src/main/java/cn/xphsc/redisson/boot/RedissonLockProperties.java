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
package cn.xphsc.redisson.boot;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.Ordered;


/**
 * {@link }
 * @author <a href="xiongpeih@163.com">huipei.x</a>
 * @description:
 * @since 1.0.0
 */
@ConfigurationProperties(prefix = RedissonLockProperties.PREFIX)
public class RedissonLockProperties {

    public static final String PREFIX = RedissonProperties.PREFIX+"lock";

    private long waitTime = 60;
    private long leaseTime = 60;

    /**
     * 锁前缀 默认为服务名 可通过此方式覆盖
     */
    private String lockPrefix;
    private int order = Ordered.LOWEST_PRECEDENCE-11;


    public long getWaitTime() {
        return waitTime;
    }

    public void setWaitTime(long waitTime) {
        this.waitTime = waitTime;
    }

    public long getLeaseTime() {
        return leaseTime;
    }

    public void setLeaseTime(long leaseTime) {
        this.leaseTime = leaseTime;
    }

    public String getLockPrefix() {
        return lockPrefix;
    }

    public void setLockPrefix(String lockPrefix) {
        this.lockPrefix = lockPrefix;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }
}
