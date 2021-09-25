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

import cn.xphsc.redisson.core.distributedlock.RedissonLockKeyProvider;
import cn.xphsc.redisson.core.distributedlock.LockInfoProvider;
import cn.xphsc.redisson.core.distributedlock.advice.LockPointcutAdvisor;
import cn.xphsc.redisson.core.distributedlock.advice.RedissonLockRegistrar;
import cn.xphsc.redisson.core.distributedlock.lock.RedissonLockFactory;
import org.redisson.api.RedissonClient;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.*;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

/**
 * {@link }
 * @author <a href="xiongpeih@163.com">huipei.x</a>
 * @description:
 * @since 1.0.0
 */
@Configuration
@AutoConfigureAfter(RedisAutoConfiguration.class)
@EnableConfigurationProperties(RedissonLockProperties.class)
@Import({RedissonLockRegistrar.class})
@ConditionalOnProperty(prefix = RedissonProperties.PREFIX+"lock",
        name = "enabled",
        matchIfMissing=true,
        havingValue = "true")
public class RedissonLockAutoConfiguration implements Ordered {
    private RedissonLockProperties redissonLockProperties;

    public RedissonLockAutoConfiguration(RedissonLockProperties redissonLockProperties){
        this.redissonLockProperties= redissonLockProperties;
    }

    @Bean
    public LockInfoProvider lockInfoProvider() {
        return new LockInfoProvider(redissonLockProperties,redissonLockKeyProvider());
    }
   @Bean
    public RedissonLockKeyProvider redissonLockKeyProvider() {
        return new RedissonLockKeyProvider();
    }

    @Bean
    public RedissonLockFactory redissonLockFactory(RedissonClient redisson) {
        return new RedissonLockFactory(redisson);
    }

    @Override
    public int getOrder() {
        return redissonLockProperties.getOrder();
    }
}

