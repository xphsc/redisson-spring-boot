package com.xphsc.example.lock;

import cn.xphsc.redisson.annotation.RedissonLock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class ExampleLockServiceImpl implements ExampleLockService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExampleLockServiceImpl.class);

    private final ConcurrentMap<String, AtomicInteger> activeExecutions =
            new ConcurrentHashMap<String, AtomicInteger>();

    @Override
    @RedissonLock(
            name = "example-order",
            keys = {"#orderId"},
            waitTime = 1,
            leaseTime = -1,
           lockTimeoutStrategyBean = "exampleLockTimeoutPolicy",
            releaseTimeoutStrategyBean = "exampleReleaseTimeoutPolicy")
    public String execute(String orderId, long holdMillis) {
        return executeBusiness("example-order", orderId, holdMillis);
    }

    @Override
    @RedissonLock(
            name = "example-order-fixed",
            keys = {"#orderId"},
            waitTime = 1,
            leaseTime = 1,
            lockTimeoutStrategyBean = "exampleLockTimeoutPolicy",
            releaseTimeoutStrategyBean = "exampleReleaseTimeoutPolicy")
    public String executeWithFixedLease(String orderId, long holdMillis) {
        return executeBusiness("fixed-lease-1s", orderId, holdMillis);
    }

    @Override
    @RedissonLock(
            name = "example-order-async",
            keys = {"#orderId"},
            waitTime = 1,
            leaseTime = 10,
            lockTimeoutStrategyBean = "exampleLockTimeoutPolicy",
            releaseTimeoutStrategyBean = "exampleReleaseTimeoutPolicy")
    public CompletionStage<String> executeAsync(String orderId, long holdMillis) {
        return CompletableFuture.supplyAsync(
                () -> executeBusiness("async", orderId, holdMillis));
    }

    private String executeBusiness(String mode, String orderId, long holdMillis) {
        AtomicInteger counter = activeExecutions.computeIfAbsent(
                orderId, key -> new AtomicInteger());
        int active = counter.incrementAndGet();
        boolean overlap = active > 1;
        long startedAt = System.currentTimeMillis();
        LOGGER.info(
                "Lock example started: mode={}, orderId={}, active={}, holdMillis={}",
                mode, orderId, active, holdMillis);
        try {
            sleep(holdMillis);
            return "mode=" + mode
                    + ", orderId=" + orderId
                    + ", startedAt=" + startedAt
                    + ", finishedAt=" + System.currentTimeMillis()
                    + ", overlap=" + overlap;
        } finally {
            if (counter.decrementAndGet() == 0) {
                activeExecutions.remove(orderId, counter);
            }
            LOGGER.info("Lock example finished: mode={}, orderId={}", mode, orderId);
        }
    }

    private void sleep(long milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Async example interrupted", e);
        }
    }
}
