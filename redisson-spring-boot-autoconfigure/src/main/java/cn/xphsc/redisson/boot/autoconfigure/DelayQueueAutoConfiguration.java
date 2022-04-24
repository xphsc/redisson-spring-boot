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
package cn.xphsc.redisson.boot.autoconfigure;


import cn.xphsc.redisson.boot.RedissonProperties;
import cn.xphsc.redisson.core.delayqueue.DelayQueueListenerBeanProcessor;
import cn.xphsc.redisson.core.delayqueue.DelayQueueListenerRegistry;
import cn.xphsc.redisson.core.delayqueue.DelayQueueTemplate;
import org.redisson.api.RedissonClient;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * {@link }
 * @author <a href="xiongpeih@163.com">huipei.x</a>
 * @description:
 * @since 1.0.0
 */
@Configuration
@ConditionalOnProperty(prefix = RedissonProperties.PREFIX+".delayqueue",
        name = "enabled",
        matchIfMissing=true,
        havingValue = "true")
public class DelayQueueAutoConfiguration {


    @Bean
    public DelayQueueListenerRegistry delayQueueListenerRegistry(RedissonClient redissonClient) {
        return new DelayQueueListenerRegistry(redissonClient);
    }


    @Bean
    public DelayQueueListenerBeanProcessor delayQueueListenerBeanProcessor(DelayQueueListenerRegistry listenerRegistry) {
        return new DelayQueueListenerBeanProcessor(listenerRegistry);
    }

    @Bean
    public DelayQueueTemplate delayQueueTemplate(RedissonClient redissonClient) {
        return new DelayQueueTemplate(redissonClient);
    }
}
