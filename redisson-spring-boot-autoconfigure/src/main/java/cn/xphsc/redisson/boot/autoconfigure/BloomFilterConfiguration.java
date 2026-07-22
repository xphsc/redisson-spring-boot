/*
 * Copyright (c) 2023 huipei.x
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

import cn.xphsc.redisson.core.bloomfilter.BloomFilterAnnotationProcessor;
import org.redisson.api.RedissonClient;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * {@link }
 * @author <a href="xiongpeih@163.com">huipei.x</a>
 * @description: Bloom filter configuration initialization
 * @since 1.0.3
 */
@Configuration
@ConditionalOnClass(RedissonClient.class)
@AutoConfigureAfter(RedissonAutoConfiguration.class)
@ConditionalOnBean(RedissonClient.class)
@ConditionalOnExpression("${spring.redis.bloomfilter.enabled:${spring.redis.loomfilter.enabled:true}}")
public class BloomFilterConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public static BloomFilterAnnotationProcessor bloomFilterAnnotationProcessor(RedissonClient redissonClient){
        return new BloomFilterAnnotationProcessor(redissonClient);
    }
}
