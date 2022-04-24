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

import cn.xphsc.redisson.core.delayqueue.DelayQueueListener;

import java.lang.annotation.*;

/**
 * {@link }
 * @author <a href="xiongpeih@163.com">huipei.x</a>
 * @description: Dead letter queue
 * For reference, the example
 * @Component public class MyDelayQueueListener{
 *   @RedissonDelayQueueListener(queueName = "delay-message-queue-name")
 *   public void onMessage(DelayMessage delayMessage) {}
 *   }
 * @see DelayQueueListener
 * @since 1.0.0
 */
@Target({ElementType.TYPE,ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RedissonDelayQueueListener {
    /**
     *Dead letter queue name
     */
    String queueName();
}
