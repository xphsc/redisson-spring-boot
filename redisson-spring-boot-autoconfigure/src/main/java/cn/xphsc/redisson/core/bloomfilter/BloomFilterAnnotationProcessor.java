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
package cn.xphsc.redisson.core.bloomfilter;

import cn.xphsc.redisson.annotation.BloomFilter;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * {@link }
 * @author <a href="xiongpeih@163.com">huipei.x</a>
 * @description:
 * @since 1.0.3
 */
public class BloomFilterAnnotationProcessor implements BeanPostProcessor {

    private final RedissonClient redissonClient;

    private final Map<String, RBloomFilter<Object>> bloomFilterMap = new ConcurrentHashMap<>();

    public BloomFilterAnnotationProcessor(RedissonClient redissonClient) {
        this.redissonClient = redissonClient;
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) {
        processBloomFilter(bean);
        return bean;
    }

    private void processBloomFilter(Object bean) {
        ReflectionUtils.doWithFields(bean.getClass(), field -> {
            BloomFilter annotation = field.getAnnotation(BloomFilter.class);
            if (annotation == null) {
                return;
            }
            if (Modifier.isStatic(field.getModifiers())) {
                throw new IllegalStateException("@BloomFilter does not support static fields: " + field);
            }
            if (!RBloomFilter.class.isAssignableFrom(field.getType())) {
                throw new IllegalStateException("@BloomFilter field must implement "
                        + RBloomFilter.class.getName() + ": " + field);
            }

            RBloomFilter<Object> bloomFilter = redissonClient.getBloomFilter(annotation.name());
            bloomFilter.tryInit(annotation.expectedElements(), annotation.falseProbability());
            ReflectionUtils.makeAccessible(field);
            ReflectionUtils.setField(field, bean, bloomFilter);
            bloomFilterMap.put(annotation.name(), bloomFilter);
        });
    }

}
