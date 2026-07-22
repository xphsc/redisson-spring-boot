package com.xphsc.example.queue;

import cn.xphsc.redisson.annotation.RedissonQueueListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class ResilientTopicListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(ResilientTopicListener.class);

    @RedissonQueueListener(queueName = ExampleQueueNames.TOPIC)
    public void onMessage(ExampleMessage message) {
        int attempt = message.nextAttempt();
        if (attempt <= message.getFailTimes()) {
            throw new IllegalStateException(
                    "Simulated topic failure, attempt=" + attempt + ", messageId=" + message.getId());
        }
        LOGGER.info("Topic message consumed successfully: {}", message);
    }
}
