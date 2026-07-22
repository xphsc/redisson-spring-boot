package com.xphsc.example.queue;

import cn.xphsc.redisson.annotation.RedissonDelayQueueListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class ResilientDelayQueueListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(ResilientDelayQueueListener.class);

    @RedissonDelayQueueListener(queueName = ExampleQueueNames.DELAY_QUEUE)
    public void onMessage(ExampleMessage message) {
        int attempt = message.nextAttempt();
        if (attempt <= message.getFailTimes()) {
            throw new IllegalStateException(
                    "Simulated delay failure, attempt=" + attempt + ", messageId=" + message.getId());
        }
        LOGGER.info("Delayed message consumed successfully: {}", message);
    }
}
