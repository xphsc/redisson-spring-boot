package com.xphsc.test.queue;

import cn.xphsc.redisson.annotation.RedissonDelayQueueListener;
import org.springframework.stereotype.Component;

/**
 * {@link }
 *
 * @author <a href="xiongpeih@163.com">huipei.x</a>
 * @description:
 * @since 1.0.0
 */
@Component
public class MyDelayQueueListener {
    @RedissonDelayQueueListener(queueName = "delay-message-queue-name")
    public void onMessage(DelayMessage delayMessage) {
        System.out.println("--delayMessage---"+delayMessage.getName());
    }
    @RedissonDelayQueueListener(queueName = "delay1-message-queue-name")
    public void onMessage1(DelayMessage delayMessage) {
        System.out.println("---delayMessage1----"+delayMessage.getName());
    }

}
