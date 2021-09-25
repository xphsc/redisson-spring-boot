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


import org.redisson.api.RedissonClient;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.support.GenericApplicationContext;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

public class DelayQueueListenerRegistry implements ApplicationContextAware, SmartInitializingSingleton {
    private ApplicationContext applicationContext;
    private final Map<String, DelayQueueListener> delayQueueListeners = new HashMap<>();
    private AtomicLong counter = new AtomicLong(0);
    private final RedissonClient redissonClient;

    public DelayQueueListenerRegistry(RedissonClient redissonClient) {
        this.redissonClient = redissonClient;
    }

    public void registerListener(DelayQueueListener listener) {
        if (delayQueueListeners.putIfAbsent(listener.queueName(), listener) != null) {
            throw new RuntimeException(String.format("Delay queue name:{} already registered", listener.queueName()));
        }
    }

    @Override
    public void afterSingletonsInstantiated() {
        delayQueueListeners.forEach(this::registerContainer);
    }

    private void registerContainer(String beanName, DelayQueueListener delayQueueListener) {
        String containerBeanName = String.format("%s_%s", DelayQueueListenerContainer.class.getName(),
                counter.incrementAndGet());
        GenericApplicationContext genericApplicationContext = (GenericApplicationContext) applicationContext;
       BeanDefinitionBuilder beanDefinitionBuilder = BeanDefinitionBuilder.genericBeanDefinition(DelayQueueListenerContainer.class);
        DelayQueueListenerContainer delayQueueListenerContainer=createListenerContainer(containerBeanName, delayQueueListener);
        beanDefinitionBuilder.addPropertyValue("distinationQueue",delayQueueListenerContainer.getDistinationQueue());
        beanDefinitionBuilder.addPropertyValue("listener",delayQueueListenerContainer.getListener());
        genericApplicationContext.registerBeanDefinition(containerBeanName,beanDefinitionBuilder.getBeanDefinition());
        DelayQueueListenerContainer container = genericApplicationContext.getBean(containerBeanName,
                DelayQueueListenerContainer.class);
        if (!container.isRunning()) {
            try {
                container.start();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    private DelayQueueListenerContainer createListenerContainer(String containerBeanName, DelayQueueListener delayQueueListener) {
        DelayQueueListenerContainer delayQueueListenerContainer = new DelayQueueListenerContainer();
        delayQueueListenerContainer.setDistinationQueue(redissonClient.getBlockingDeque(delayQueueListener.queueName()));
        delayQueueListenerContainer.setListener(delayQueueListener);
        return delayQueueListenerContainer;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
