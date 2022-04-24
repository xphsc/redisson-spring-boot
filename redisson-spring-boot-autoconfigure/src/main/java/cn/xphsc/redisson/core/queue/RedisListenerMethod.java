/*
 * Copyright (c) 2022 huipei.x
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

import org.springframework.context.ApplicationContext;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
/**
 * {@link }
 * @author <a href="xiongpeih@163.com">huipei.x</a>
 * @description:
 * @since 1.0.0
 */
public class RedisListenerMethod {
    private Object bean;

    private String beanName;

    private Method targetMethod;

    private String methodParameterClassName;

    private Class<?> parameterClass;

    private Type parameterType;


    public String getBeanName() {
        return beanName;
    }

    public void setBeanName(String beanName) {
        this.beanName = beanName;
    }

    public Method getTargetMethod() {
        return targetMethod;
    }

    public void setTargetMethod(Method targetMethod) {
        this.targetMethod = targetMethod;
    }

    public void setParameterClass(Class<?> parameterClass){
        this.parameterClass = parameterClass;
    }

    public Class<?> getParameterClass() {
        return parameterClass;
    }

    public void setParameterType(Type parameterType) {
        this.parameterType = parameterType;
    }

    public Type getParameterType() {
        return parameterType;
    }

    public Object getBean(ApplicationContext applicationContext) {
        if (bean == null) {
            synchronized (this) {
                if (bean == null) {
                    bean = applicationContext.getBean(beanName);
                }
            }
        }
        return bean;
    }

    public void setBean(Object bean) {
        this.bean = bean;
    }


    public String getMethodParameterClassName() {
        return methodParameterClassName;
    }

    public void setMethodParameterClassName(String methodParameterClassName) {
        this.methodParameterClassName = methodParameterClassName;
    }
}
