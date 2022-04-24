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


import cn.xphsc.redisson.annotation.RedissonQueueListener;
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
/**
 * {@link }
 * @author <a href="xiongpeih@163.com">huipei.x</a>
 * @description:
 * @since 1.0.0
 */
public class QueueListenerRegistry implements ApplicationContextAware, SmartInitializingSingleton {
    private ApplicationContext applicationContext;
    private static final Map<String, List<RedisListenerMethod>> redisListeners = new HashMap<>();
    private AtomicLong counter = new AtomicLong(0);
    private final RedissonClient redissonClient;
    String queueName;
    public QueueListenerRegistry(RedissonClient redissonClient) {
        this.redissonClient = redissonClient;
    }


    public void registerListener(Class<?> clazz,String beanName) {
        Method[] methods = ReflectionUtils.getAllDeclaredMethods(clazz);
        for (Method method : methods) {
            AnnotationAttributes annotationAttributes = AnnotatedElementUtils
                    .findMergedAnnotationAttributes(method, RedissonQueueListener.class, false, false);
            if (null != annotationAttributes) {
                Class<?>[] parameterTypes = method.getParameterTypes();
                if (parameterTypes.length == 1) {
                    queueName = (String) annotationAttributes.get("queueName");
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
                    throw new RuntimeException("有@RedissonQueueListener注解的方法有且仅能包含一个参数");
                }
            }
        }
    }

    @Override
    public void afterSingletonsInstantiated() {
            redisListeners.forEach(this::registerContainer);
    }







    private void registerContainer(String topic, List<RedisListenerMethod> redisListenerMethods) {
        String containerBeanName = String.format("%s_%s", QueueListenerContainer.class.getName(),
                counter.incrementAndGet());
        GenericApplicationContext genericApplicationContext = (GenericApplicationContext) applicationContext;
        BeanDefinitionBuilder beanDefinitionBuilder = BeanDefinitionBuilder.genericBeanDefinition(QueueListenerContainer.class);
            beanDefinitionBuilder.addPropertyValue("topic",redissonClient.getTopic(topic));
            beanDefinitionBuilder.addPropertyValue("applicationContext", this.applicationContext);
            beanDefinitionBuilder.addPropertyValue("redisListenerMethods", redisListenerMethods);
            genericApplicationContext.registerBeanDefinition(containerBeanName, beanDefinitionBuilder.getBeanDefinition());
           QueueListenerContainer container = genericApplicationContext.getBean(containerBeanName,
                    QueueListenerContainer.class);
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
