/*
 * Copyright (c) 2021 huipei.x
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cn.xphsc.redisson.core.queue;


import org.redisson.api.RTopic;
import org.redisson.api.listener.MessageListener;
import org.slf4j.MDC;
import org.springframework.context.ApplicationContext;
import org.springframework.context.SmartLifecycle;
import org.springframework.util.ReflectionUtils;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * {@link }
 * @author <a href="xiongpeih@163.com">huipei.x</a>
 * @description:
 * @since 1.0.0
 */
@SuppressWarnings("ALL")
public class QueueListenerContainer implements SmartLifecycle {
    private static final RedisListenerErrorHandler FALLBACK_ERROR_HANDLER =
            new LoggingRedisListenerErrorHandler();

    private RTopic topic;
    private volatile boolean running;
    private ExecutorService threadPool;
    private volatile boolean stop = false;
    private volatile int listenerId = -1;
    private String queueName;
    private List<RedisListenerMethod> redisListenerMethods;
    private ApplicationContext applicationContext;
    private RedisListenerErrorHandler errorHandler = new LoggingRedisListenerErrorHandler();

    @Override
    public synchronized void start() {
        if (running) {
            return;
        }
        if (topic == null) {
            throw new IllegalStateException("Redis topic must be configured before starting listener " + queueName);
        }
        stop = false;
        if (threadPool == null || threadPool.isShutdown()) {
            threadPool = new ThreadPoolExecutor(1,
                    Runtime.getRuntime().availableProcessors(),
                    30,
                    TimeUnit.MINUTES,
                    new SynchronousQueue(),
                    new QueueThreadFactory(queueName()),
                    new ThreadPoolExecutor.CallerRunsPolicy()
            );
        }
        try {
            listenerId = topic.addListener(Object.class, new MessageListener<Object>() {
                @Override
                public void onMessage(CharSequence channel, Object message) {
                    if (message == null || stop) {
                        return;
                    }
                    try {
                        threadPool.submit(() -> handleMethodMessage(message));
                    } catch (RuntimeException e) {
                        handleError(message, e);
                    }
                }
            });
            running = true;
        } catch (RuntimeException e) {
            threadPool.shutdownNow();
            handleError(null, e);
            throw e;
        }
    }

    private void handleMethodMessage(Object message) {
        MDC.put("UUID", UUID.randomUUID().toString());
        try {
            if (redisListenerMethods != null) {
                for (RedisListenerMethod redisListenerMethod : redisListenerMethods) {
                    Method targetMethod = redisListenerMethod.getTargetMethod();
                    try {
                        ReflectionUtils.makeAccessible(targetMethod);
                        targetMethod.invoke(redisListenerMethod.getBean(applicationContext), message);
                    } catch (InvocationTargetException e) {
                        handleError(message, e.getTargetException());
                    } catch (Throwable e) {
                        handleError(message, e);
                    }
                }
            }
        } finally {
            MDC.remove("UUID");
        }
    }

    private void handleError(Object message, Throwable error) {
        try {
            errorHandler.handle(queueName, message, error);
        } catch (Throwable handlerError) {
            handlerError.addSuppressed(error);
            FALLBACK_ERROR_HANDLER.handle(queueName, message, handlerError);
        }
    }



    public ExecutorService getThreadPool() {
        return threadPool;
    }

    public void setThreadPool(ExecutorService threadPool) {
        this.threadPool = threadPool;
    }

    public boolean isStop() {
        return stop;
    }

    public void setStop(boolean stop) {
        this.stop = stop;
    }

    @Override
    public synchronized void stop() {
        stop = true;
        if (topic != null && listenerId >= 0) {
            try {
                topic.removeListener(listenerId);
            } catch (RuntimeException e) {
                handleError(null, e);
            } finally {
                listenerId = -1;
            }
        }
        if (threadPool != null) {
            threadPool.shutdownNow();
        }
        running = false;
    }

    @Override
    public boolean isRunning() {
        return running;
    }

    public String queueName() {
        return this.queueName;
    }

    public void setQueueName(String queueName) {
        this.queueName = queueName;
    }

    public List<RedisListenerMethod> getRedisListenerMethods() {
        return redisListenerMethods;
    }

    public void setRedisListenerMethods(List<RedisListenerMethod> redisListenerMethods) {
        this.redisListenerMethods = redisListenerMethods;
    }

    public ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    public RedisListenerErrorHandler getErrorHandler() {
        return errorHandler;
    }

    public void setErrorHandler(RedisListenerErrorHandler errorHandler) {
        if (errorHandler != null) {
            this.errorHandler = errorHandler;
        }
    }

    public RTopic getTopic() {
        return topic;
    }

    public void setTopic(RTopic topic) {
        this.topic = topic;
    }

    public String getQueueName() {
        return queueName;
    }

    public boolean isAutoStartup() {
        return false;
    }

    public void stop(Runnable runnable) {
        try {
            stop();
        } finally {
            runnable.run();
        }
    }



    public int getPhase() {
        return 0;
    }
}
