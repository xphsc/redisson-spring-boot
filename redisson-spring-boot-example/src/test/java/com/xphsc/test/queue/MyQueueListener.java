package com.xphsc.test.queue;

import cn.xphsc.redisson.annotation.RedissonDelayQueueListener;
import cn.xphsc.redisson.annotation.RedissonQueueListener;
import org.springframework.stereotype.Component;

/**
 * {@link }
 *
 * @author <a href="xiongpeih@163.com">huipei.x</a>
 * @description:
 * @since 1.0.0
 */
//RedissonDelayQueueListener(queueName = "delay-message-queue-name")
@Component
public class MyQueueListener/* implements DelayQueueListener<DelayMessage>*/ {
  //  @Override
 @RedissonQueueListener(queueName = "message333-message-queue-name")
    public void onMessage(DelayMessage delayMessage) {
        System.out.println("---message333--------"+delayMessage.getName());
    }
   @RedissonQueueListener(queueName = "message444-message-queue-name")
    public void onMessage1(DelayMessage delayMessage) {
        System.out.println("message444------"+delayMessage.getName());
    }

}
