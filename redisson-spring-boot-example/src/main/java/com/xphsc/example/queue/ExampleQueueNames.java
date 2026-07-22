package com.xphsc.example.queue;

public final class ExampleQueueNames {

    public static final String TOPIC = "example.topic.orders";
    public static final String TOPIC1 = "example.topic1.orders";
    public static final String BLOCKING_QUEUE = "example.queue.orders";
    public static final String DELAY_QUEUE = "example.delay.orders";
    public static final String TOPIC_DEAD_LETTER_QUEUE = TOPIC + ".dlq";
    public static final String DELAY_DEAD_LETTER_QUEUE = DELAY_QUEUE + ".dlq";

    private ExampleQueueNames() {
    }
}
