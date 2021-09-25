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
package cn.xphsc.redisson.core.distributedlock.handler.release;


import cn.xphsc.redisson.core.distributedlock.entity.LockInfo;

/**
 * {@link }
 * @author <a href="xiongpeih@163.com">huipei.x</a>
 * @description: 获取锁超时的处理逻辑接口
 * @since 1.0.0
 */
public interface ReleaseTimeoutHandler {

    void handle(LockInfo lockInfo);
}
