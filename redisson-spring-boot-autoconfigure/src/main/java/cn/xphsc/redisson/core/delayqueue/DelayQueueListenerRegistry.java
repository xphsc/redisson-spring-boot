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


import cn.xphsc.redisson.annotation.RedissonDelayQueueListener;
import cn.xphsc.redisson.core.queue.RedisListenerMethod;
import org.redisson.api.RBlockingDeque;
import org.redisson.api.RedissonClient;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.util.ReflectionUtils;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

public class DelayQueueListenerRegistry implements ApplicationContextAware, SmartInitializingSingleton {
    private ApplicationContext applicationContext;
    private final Map<String, DelayQueueListener> delayQueueListeners = new HashMap<>();
    private static final Map<String, List<RedisListenerMethod>> redisListeners = new HashMap<>();
    private AtomicLong counter = new AtomicLong(0);
    private final RedissonClient redissonClient;
    private String queueName;
    public DelayQueueListenerRegistry(RedissonClient redissonClient) {
        this.redissonClient = redissonClient;
    }

    public void registerListener(DelayQueueListener listener) {
        RedissonDelayQueueListener redissonDelayQueryListener=  listener.getClass().getAnnotation(RedissonDelayQueueListener.class);
        this.queueName=redissonDelayQueryListener.queueName();
        if (delayQueueListeners.putIfAbsent(queueName, listener) != null) {
            throw new RuntimeException(String.format("Delay queue name:{} already registered", queueName));
        }
    }

    public void registerListener(Class<?> clazz,String beanName) {
        Method[] methods = ReflectionUtils.getAllDeclaredMethods(clazz);
        for (Method method : methods) {
            AnnotationAttributes annotationAttributes = AnnotatedElementUtils
                    .findMergedAnnotationAttributes(method, RedissonDelayQueueListener.class, false, false);
            if (null != annotationAttributes) {
                Class<?>[] parameterTypes = method.getParameterTypes();
                if (parameterTypes.length == 1) {
                    String queueName = (String) annotationAttributes.get("queueName");
                    Type[] genericParameterTypes = method.getGenericParameterTypes();
                    RedisListenerMethod rlm = new RedisListenerMethod();
                    rlm.setBeanName(beanName);
                    rlm.setTargetMethod(method);
                    rlm.setMethodParameterClassName(parameterTypes[0].getName());
                    rlm.setParameterClass(parameterTypes[0]);
                    rlm.setParameterType(genericParameterTypes[0]);
                    if (!redisListeners.containsKey(queueName)) {
                        redisListeners.put(queueName, new LinkedList<>());
                    }
                    redisListeners.get(queueName).add(rlm);
                } else {
                    throw new RuntimeException("有@RedissonDelayQueueListener注解的方法有且仅能包含一个参数");
                }
            }

        }
    }

    @Override
    public void afterSingletonsInstantiated() {
        if(!delayQueueListeners.isEmpty()&&delayQueueListeners.size()>0){
            delayQueueListeners.forEach(this::registerContainer);
        }else{
            redisListeners.forEach(this::registerContainer);
        }
    }




    private void registerContainer(String beanName, DelayQueueListener delayQueueListener) {
        String containerBeanName = String.format("%s_%s", DelayQueueListenerContainer.class.getName(),
                counter.incrementAndGet());
        GenericApplicationContext genericApplicationContext = (GenericApplicationContext) applicationContext;
        BeanDefinitionBuilder beanDefinitionBuilder = BeanDefinitionBuilder.genericBeanDefinition(DelayQueueListenerContainer.class);
        DelayQueueListenerContainer delayQueueListenerContainer=createListenerContainer(containerBeanName, delayQueueListener);
        beanDefinitionBuilder.addPropertyValue("distinationQueue",delayQueueListenerContainer.getDistinationQueue());
        beanDefinitionBuilder.addPropertyValue("listener",delayQueueListenerContainer.getListener());
        beanDefinitionBuilder.addPropertyValue("queueName",delayQueueListenerContainer.queueName());
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
        RBlockingDeque<Object> blockingDeque = redissonClient.getBlockingDeque(queueName);
         redissonClient.getDelayedQueue(blockingDeque);
        delayQueueListenerContainer.setDistinationQueue(blockingDeque);
        delayQueueListenerContainer.setListener(delayQueueListener);
        delayQueueListenerContainer.setQueueName(queueName);
        return delayQueueListenerContainer;
    }


    private void registerContainer(String queueKey, List<RedisListenerMethod> redisListenerMethods) {
        String containerBeanName = String.format("%s_%s", DelayQueueListenerContainer.class.getName(),
                counter.incrementAndGet());
        GenericApplicationContext genericApplicationContext = (GenericApplicationContext) applicationContext;
        BeanDefinitionBuilder beanDefinitionBuilder = BeanDefinitionBuilder.genericBeanDefinition(DelayQueueListenerContainer.class);
         RBlockingDeque<Object> blockingDeque = redissonClient.getBlockingDeque(queueKey);
            redissonClient.getDelayedQueue(blockingDeque);
            beanDefinitionBuilder.addPropertyValue("distinationQueue",blockingDeque);
            beanDefinitionBuilder.addPropertyValue("queueName", queueKey);
            beanDefinitionBuilder.addPropertyValue("applicationContext", this.applicationContext);
                beanDefinitionBuilder.addPropertyValue("redisListenerMethods", redisListenerMethods);
            genericApplicationContext.registerBeanDefinition(containerBeanName, beanDefinitionBuilder.getBeanDefinition());
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

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    public ApplicationContext getApplicationContext() {
        return applicationContext;
    }
}
