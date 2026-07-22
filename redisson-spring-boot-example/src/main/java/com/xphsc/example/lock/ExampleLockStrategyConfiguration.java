package com.xphsc.example.lock;

import cn.xphsc.redisson.core.distributedlock.strategy.LockTimeoutOutcome;
import cn.xphsc.redisson.core.distributedlock.strategy.LockTimeoutPolicy;
import cn.xphsc.redisson.core.distributedlock.strategy.ReleaseTimeoutPolicy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

@Configuration
public class ExampleLockStrategyConfiguration {

    private static final Logger LOGGER =
            LoggerFactory.getLogger(ExampleLockStrategyConfiguration.class);

    @Bean("exampleLockTimeoutPolicy")
    public LockTimeoutPolicy exampleLockTimeoutPolicy() {
        return context -> {
            String message = "lock timeout: " + context.getLockInfo().getName();
            Class<?> returnType = context.getInvocation().getMethod().getReturnType();
            if (CompletionStage.class.isAssignableFrom(returnType)) {
                return LockTimeoutOutcome.returnValue(
                        CompletableFuture.completedFuture(message));
            }
            return LockTimeoutOutcome.  returnValue(message);
        };
    }

    @Bean("exampleReleaseTimeoutPolicy")
    public ReleaseTimeoutPolicy exampleReleaseTimeoutPolicy() {
        return context -> LOGGER.warn(
                "Lock was not released normally: {}", context.getLockInfo().getName());
    }
}
