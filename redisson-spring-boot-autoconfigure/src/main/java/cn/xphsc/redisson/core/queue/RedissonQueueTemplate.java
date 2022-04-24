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



import org.redisson.api.RTopic;
import org.redisson.api.RedissonClient;



/**
 * {@link }
 * @author <a href="xiongpeih@163.com">huipei.x</a>
 * @description: Message queue template
 * @since 1.0.0
 */
public class RedissonQueueTemplate<S> {

    private final RedissonClient redissonClient;

    public RedissonQueueTemplate(RedissonClient redissonClient) {
        this.redissonClient = redissonClient;
    }

    public boolean send(String queueName, S message) {
        try {
            RTopic topic = redissonClient.getTopic(queueName);
            topic.publish(message);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    public boolean sendAsync(String queueName, S message) {
        try {
            RTopic topic = redissonClient.getTopic(queueName);
            topic.publishAsync(message);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

}
