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
package cn.xphsc.redisson.core.distributedlock.advice;


import cn.xphsc.redisson.utils.BeanRegistrarUtils;
import org.springframework.aop.config.AopConfigUtils;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.Ordered;
import org.springframework.core.type.AnnotationMetadata;

/**
 * {@link ImportBeanDefinitionRegistrar}
 * @author <a href="xiongpeih@163.com">huipei.x</a>
 * @description:
 * @since 1.0.0
 */
public class RedissonLockRegistrar implements ImportBeanDefinitionRegistrar  {
    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        BeanDefinitionBuilder bdb = BeanRegistrarUtils.genericBeanDefinition(LockPointcutAdvisor.class);
        bdb.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
        bdb.addPropertyValue("order", Ordered.LOWEST_PRECEDENCE);
        BeanDefinition bd = bdb.getBeanDefinition();
        String beanName = LockPointcutAdvisor.class.getName();
        BeanRegistrarUtils.registerBeanDefinitionIfNotExists(registry, beanName, bd);

    }


}
