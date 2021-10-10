package cn.xphsc.redisson.example.queue;

import cn.xphsc.redisson.annotation.RedissonDelayQueryListener;
import cn.xphsc.redisson.core.delayqueue.DelayQueueListener;
import org.springframework.stereotype.Component;

/**
 * {@link }
 *
 * @author <a href="xiongpeih@163.com">huipei.x</a>
 * @description:
 * @since 1.0.0
 */
@RedissonDelayQueryListener(queueName = "delay-message-queue-name")
@Component
public class MyDelayQueueListener implements DelayQueueListener<DelayMessage> {
    @Override
    public void onMessage(DelayMessage delayMessage) {
        System.out.println("delayMessage"+delayMessage.getName());
    }

}
