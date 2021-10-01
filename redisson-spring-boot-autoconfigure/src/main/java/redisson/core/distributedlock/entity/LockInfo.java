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


/**
 * {@link }
 * @author <a href="xiongpeih@163.com">huipei.x</a>
 * @description: 锁基本信息
 * @since 1.0.0
 */
public class LockInfo {

    private LockType type;
    private String name;
    private long waitTime;
    private long leaseTime;

    public LockInfo() {
    }

    public LockInfo(LockType type, String name, long waitTime, long leaseTime) {
        this.type = type;
        this.name = name;
        this.waitTime = waitTime;
        this.leaseTime = leaseTime;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

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

    public LockType getType() {
        return type;
    }

    public void setType(LockType type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "LockInfo{" +
                "type=" + type +
                ", name='" + name + '\'' +
                ", waitTime=" + waitTime +
                ", leaseTime=" + leaseTime +
                '}';
    }
}
