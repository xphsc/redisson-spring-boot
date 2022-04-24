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


import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;


/**
 * {@link }
 * @author <a href="xiongpeih@163.com">huipei.x</a>
 * @description:
 * @since 1.0.0
 */
public class QueueListenerBeanProcessor implements BeanPostProcessor {

    private final QueueListenerRegistry queueListenerRegistry;

    public QueueListenerBeanProcessor( QueueListenerRegistry queueListenerRegistry) {
        this.queueListenerRegistry = queueListenerRegistry;
    }



    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        Class<?> clazz = bean.getClass();
        this.queueListenerRegistry.registerListener(clazz,beanName);
        return bean;

    }
    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }
}
