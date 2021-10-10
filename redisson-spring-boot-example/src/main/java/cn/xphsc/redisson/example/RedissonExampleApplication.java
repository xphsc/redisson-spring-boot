package com.xphsc.test.example;

import cn.xphsc.redisson.core.delayqueue.DelayQueueListenerRegistry;
import cn.xphsc.redisson.core.delayqueue.DelayQueueTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.function.Supplier;

@SpringBootApplication
public class ExampleApplication {
    public static void main(String[] args) {

        ConfigurableApplicationContext configurableApplicationContext= SpringApplication.run(ExampleApplication.class, args);
       /* DelayQueueTemplate delayQueueTemplate=  configurableApplicationContext.getBean(DelayQueueTemplate.class);
        System.out.println("22233454-"+delayQueueTemplate.receive("delay-message-queue-name"));*/
    }
}
   /* public <T> void registerBean(String beanName, Class<T> beanClass, Supplier<T> supplier, DelayQueueListenerRegistry.BeanDefinitionCustomizer... customizers) {
        BeanDefinitionBuilder builder = supplier != null ? BeanDefinitionBuilder.genericBeanDefinition(beanClass, supplier) : BeanDefinitionBuilder.genericBeanDefinition(beanClass);
        BeanDefinition beanDefinition = applyCustomizers(customizers).getRawBeanDefinition();
        String nameToUse = beanName != null ? beanName : beanClass.getName();
        genericApplicationContext.registerBeanDefinition(nameToUse, beanDefinition);
    }

*//*
@FunctionalInterface
public interface BeanDefinitionCustomizer {
    void customize(BeanDefinition var1);
}

    private AbstractBeanDefinition beanDefinition;
    public BeanDefinitionBuilder applyCustomizers(DelayQueueListenerRegistry.BeanDefinitionCustomizer... customizers) {
        DelayQueueListenerRegistry.BeanDefinitionCustomizer[] var2 = customizers;
        int var3 = customizers.length;

        for(int var4 = 0; var4 < var3; ++var4) {
            DelayQueueListenerRegistry.BeanDefinitionCustomizer customizer = var2[var4];
            customizer.customize(this.beanDefinition);
        }
        return BeanDefinitionBuilder.genericBeanDefinition();
    }*//*
*/