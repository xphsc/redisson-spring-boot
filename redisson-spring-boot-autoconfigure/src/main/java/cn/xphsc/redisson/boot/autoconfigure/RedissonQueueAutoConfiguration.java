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
import cn.xphsc.redisson.core.queue.QueueListenerBeanProcessor;
import cn.xphsc.redisson.core.queue.QueueListenerRegistry;
import cn.xphsc.redisson.core.queue.LoggingRedisListenerErrorHandler;
import cn.xphsc.redisson.core.queue.RedisListenerErrorHandler;
import cn.xphsc.redisson.core.queue.RedissonBlockingQueueTemplate;
import cn.xphsc.redisson.core.queue.RedissonQueueTemplate;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
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
@ConditionalOnClass(RedissonClient.class)
@ConditionalOnBean(RedissonClient.class)
@AutoConfigureAfter(RedissonAutoConfiguration.class)
@ConditionalOnProperty(prefix = RedissonProperties.PREFIX+".queue",
        name = "enabled",
        matchIfMissing=true,
        havingValue = "true")
public class RedissonQueueAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(QueueListenerRegistry.class)
    public QueueListenerRegistry queueListenerRegistry(RedissonClient redissonClient,
                                                       ObjectProvider<RedisListenerErrorHandler> errorHandlerProvider) {
        RedisListenerErrorHandler errorHandler = errorHandlerProvider.getIfAvailable();
        if (errorHandler == null) {
            errorHandler = new LoggingRedisListenerErrorHandler();
        }
        return new QueueListenerRegistry(redissonClient, errorHandler);
    }


    @Bean
    @ConditionalOnMissingBean(QueueListenerBeanProcessor.class)
    public QueueListenerBeanProcessor queueListenerBeanProcessor(QueueListenerRegistry listenerRegistry) {
        return new QueueListenerBeanProcessor(listenerRegistry);
    }

    @Bean
    @ConditionalOnMissingBean(RedissonQueueTemplate.class)
    public RedissonQueueTemplate redissonQueueTemplate(RedissonClient redissonClient) {
        return new RedissonQueueTemplate(redissonClient);
    }

    @Bean
    @ConditionalOnMissingBean(RedissonBlockingQueueTemplate.class)
    public RedissonBlockingQueueTemplate redissonBlockingQueueTemplate(RedissonClient redissonClient) {
        return new RedissonBlockingQueueTemplate(redissonClient);
    }
}
