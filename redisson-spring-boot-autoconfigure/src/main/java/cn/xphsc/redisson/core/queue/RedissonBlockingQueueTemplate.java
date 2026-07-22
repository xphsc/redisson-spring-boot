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
package cn.xphsc.redisson.core.queue;

import org.redisson.api.RBlockingQueue;
import org.redisson.api.RFuture;
import org.redisson.api.RedissonClient;

import java.util.Objects;
import java.util.concurrent.TimeUnit;


/**
 * {@link }
 * @author <a href="xiongpeih@163.com">huipei.x</a>
 * @description: Redis enqueue broadcast template. Use
 * Competitive-consumption queue template backed by {@link RBlockingQueue}.
 * Each message is removed by one consumer.
 * @since 1.0.6
 */
public class RedissonBlockingQueueTemplate<S> {

    private final RedissonClient redissonClient;

    public RedissonBlockingQueueTemplate(RedissonClient redissonClient) {
        this.redissonClient = Objects.requireNonNull(redissonClient, "redissonClient must not be null");
    }

    public boolean send(String queueName, S message) {
        return queue(queueName).offer(message);
    }

    public RFuture<Boolean> sendAsync(String queueName, S message) {
        return queue(queueName).offerAsync(message);
    }

    public S receive(String queueName) throws InterruptedException {
        return queue(queueName).take();
    }

    public S poll(String queueName, long timeout, TimeUnit unit) throws InterruptedException {
        return queue(queueName).poll(timeout, unit);
    }

    private RBlockingQueue<S> queue(String queueName) {
        return redissonClient.getBlockingQueue(queueName);
    }
}
