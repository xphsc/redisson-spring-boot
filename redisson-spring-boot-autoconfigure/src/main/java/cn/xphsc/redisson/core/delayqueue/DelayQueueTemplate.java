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
package cn.xphsc.redisson.core.delayqueue;


import org.redisson.api.RBlockingDeque;
import org.redisson.api.RDelayedQueue;
import org.redisson.api.RedissonClient;
import java.util.concurrent.TimeUnit;
/**
 * {@link }
 * @author <a href="xiongpeih@163.com">huipei.x</a>
 * @description:  Dead letter queue template
 * @since 1.0.0
 */
public class DelayQueueTemplate<S> {

    private final RedissonClient redissonClient;

    public DelayQueueTemplate(RedissonClient redissonClient) {
        this.redissonClient = redissonClient;
    }

    public boolean send(String queueName, S message, long delay, TimeUnit timeUnit) {
         RDelayedQueue<S> delayedQueue = null;
        try {
            final RBlockingDeque<S> distinationQueue = redissonClient.getBlockingDeque(queueName);
          delayedQueue = redissonClient.getDelayedQueue(distinationQueue);
            delayedQueue.offer(message, delay, timeUnit);
        } catch (Exception e) {
            return false;
        }
        return true;
    }


    public boolean sendAsync(String queueName,  S message, long delay, TimeUnit timeUnit) {
        try {
            final RBlockingDeque<S> distinationQueue = redissonClient.getBlockingDeque(queueName);
            final RDelayedQueue<S> delayedQueue = redissonClient.getDelayedQueue(distinationQueue);
            delayedQueue.offerAsync(message, delay, timeUnit);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    public S receive(String queueName) {
        S currentMessage = null;
        final RBlockingDeque<S> distinationQueue = redissonClient.getBlockingDeque(queueName);
         redissonClient.getDelayedQueue(distinationQueue);
        try {
            currentMessage = distinationQueue.take();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return currentMessage;
    }
}
