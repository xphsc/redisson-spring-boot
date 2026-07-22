package com.xphsc.example.controller;

import cn.xphsc.redisson.core.delayqueue.DelayQueueTemplate;
import cn.xphsc.redisson.core.queue.RedisDeadLetterMessage;
import cn.xphsc.redisson.core.queue.RedissonBlockingQueueTemplate;
import cn.xphsc.redisson.core.queue.RedissonQueueTemplate;
import com.xphsc.example.lock.ExampleLockService;
import com.xphsc.example.queue.ExampleMessage;
import com.xphsc.example.queue.ExampleQueueNames;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/examples/redisson")
public class RedissonExampleController {

/*    private final RedissonQueueTemplate topicTemplate;
    private final RedissonBlockingQueueTemplate<Object> blockingQueueTemplate;
    private final DelayQueueTemplate delayQueueTemplate;
    private final ExampleLockService lockService;

    public RedissonExampleController(
            RedissonQueueTemplate<ExampleMessage> topicTemplate,
            RedissonBlockingQueueTemplate<Object> blockingQueueTemplate,
            DelayQueueTemplate<ExampleMessage> delayQueueTemplate,
            ExampleLockService lockService) {
        this.topicTemplate = topicTemplate;
        this.blockingQueueTemplate = blockingQueueTemplate;
        this.delayQueueTemplate = delayQueueTemplate;
        this.lockService = lockService;
    }*/
    @Autowired
private  RedissonQueueTemplate redissonQueueTemplate;
    @Autowired
    private  RedissonBlockingQueueTemplate<Object> blockingQueueTemplate;
    @Autowired
    private  DelayQueueTemplate delayQueueTemplate;
    @Autowired
    private  ExampleLockService lockService;

    @PostMapping("/topic")
    public Map<String, Object> publishTopic(
            @RequestParam(defaultValue = "topic message") String content,
            @RequestParam(defaultValue = "0") int failTimes) {
        ExampleMessage message = message(content, failTimes);
        boolean sent = redissonQueueTemplate.send(ExampleQueueNames.TOPIC, message);
        return response("broadcast", ExampleQueueNames.TOPIC, sent, message);
    }

    @PostMapping("/delay1")
    public Map<String, Object> delay1(
            @RequestParam(defaultValue = "topic message") String content,
            @RequestParam(defaultValue = "0") int failTimes) {
        ExampleMessage message = message(content, failTimes);
        boolean sent = redissonQueueTemplate.send("message-message-queue-name", message);
        return response("broadcast", ExampleQueueNames.TOPIC, sent, message);
    }

    @PostMapping("/delay2")
    public Map<String, Object> delay2(
            @RequestParam(defaultValue = "topic message2") String content,
            @RequestParam(defaultValue = "0") int failTimes) {
        ExampleMessage message = message(content, failTimes);
        boolean sent = redissonQueueTemplate.send("message1-message-queue-name", message);
        return response("broadcast", ExampleQueueNames.TOPIC, sent, message);
    }


    @PostMapping("/delay")
    public Map<String, Object> publishDelay(
            @RequestParam(defaultValue = "delayed message") String content,
            @RequestParam(defaultValue = "1") long delaySeconds,
            @RequestParam(defaultValue = "0") int failTimes) {
        ExampleMessage message = message(content, failTimes);
        boolean sent = delayQueueTemplate.send(
                ExampleQueueNames.DELAY_QUEUE, message, delaySeconds, TimeUnit.SECONDS);
        return response("delayed", ExampleQueueNames.DELAY_QUEUE, sent, message);
    }

    @PostMapping("/queue")
    public Map<String, Object> enqueue(
            @RequestParam(defaultValue = "competitive message") String content) {
        ExampleMessage message = message(content, 0);
        boolean sent = blockingQueueTemplate.send(
                ExampleQueueNames.BLOCKING_QUEUE, message);
        return response("competitive", ExampleQueueNames.BLOCKING_QUEUE, sent, message);
    }

    @GetMapping("/queue")
    public Object pollQueue(
            @RequestParam(defaultValue = "3") long timeoutSeconds)
            throws InterruptedException {
        return blockingQueueTemplate.poll(
                ExampleQueueNames.BLOCKING_QUEUE, timeoutSeconds, TimeUnit.SECONDS);
    }

    @GetMapping("/dead-letter/topic")
    public RedisDeadLetterMessage pollTopicDeadLetter(
            @RequestParam(defaultValue = "1") long timeoutSeconds)
            throws InterruptedException {
        return (RedisDeadLetterMessage) blockingQueueTemplate.poll(
                ExampleQueueNames.TOPIC_DEAD_LETTER_QUEUE,
                timeoutSeconds,
                TimeUnit.SECONDS);
    }

    @GetMapping("/dead-letter/delay")
    public RedisDeadLetterMessage pollDelayDeadLetter(
            @RequestParam(defaultValue = "1") long timeoutSeconds)
            throws InterruptedException {
        return (RedisDeadLetterMessage) blockingQueueTemplate.poll(
                ExampleQueueNames.DELAY_DEAD_LETTER_QUEUE,
                timeoutSeconds,
                TimeUnit.SECONDS);
    }

    @GetMapping("/lock/{orderId}")
    public String lock(
            @PathVariable String orderId,
            @RequestParam(defaultValue = "3000") long holdMillis) {
        return lockService.execute(orderId, holdMillis);
    }

    @GetMapping("/lock/{orderId}/fixed-lease")
    public String fixedLeaseLock(
            @PathVariable String orderId,
            @RequestParam(defaultValue = "3000") long holdMillis) {
        return lockService.executeWithFixedLease(orderId, holdMillis);
    }

    @GetMapping("/lock/{orderId}/async")
    public CompletionStage<String> asyncLock(
            @PathVariable String orderId,
            @RequestParam(defaultValue = "3000") long holdMillis) {
        return lockService.executeAsync(orderId, holdMillis);
    }

    private ExampleMessage message(String content, int failTimes) {
        return new ExampleMessage(UUID.randomUUID().toString(), content, failTimes);
    }

    private Map<String, Object> response(
            String mode, String destination, boolean sent, ExampleMessage message) {
        Map<String, Object> response = new LinkedHashMap<String, Object>();
        response.put("mode", mode);
        response.put("destination", destination);
        response.put("sent", sent);
        response.put("message", message);
        return response;
    }
}
