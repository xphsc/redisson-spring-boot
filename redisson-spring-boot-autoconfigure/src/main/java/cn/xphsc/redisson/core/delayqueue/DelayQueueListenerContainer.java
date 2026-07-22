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
package cn.xphsc.redisson.core.delayqueue;


import cn.xphsc.redisson.core.queue.QueueThreadFactory;
import cn.xphsc.redisson.core.queue.RedisListenerMethod;
import cn.xphsc.redisson.core.queue.LoggingRedisListenerErrorHandler;
import cn.xphsc.redisson.core.queue.RedisListenerErrorHandler;
import org.redisson.api.RBlockingDeque;
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
public class DelayQueueListenerContainer implements SmartLifecycle {
    private static final RedisListenerErrorHandler FALLBACK_ERROR_HANDLER =
            new LoggingRedisListenerErrorHandler();

    private RBlockingDeque<Object> distinationQueue;
    private DelayQueueListener listener;
    private volatile boolean running;
    private ExecutorService threadPool;
    private volatile boolean stop = false;
    private volatile Thread listenerThread;
    private String queueName;
    private List<RedisListenerMethod> redisListenerMethods;
    private ApplicationContext applicationContext;
    private RedisListenerErrorHandler errorHandler = new LoggingRedisListenerErrorHandler();

    @Override
    public synchronized void start() {
        if (running) {
            return;
        }
        if (distinationQueue == null) {
            throw new IllegalStateException("Redis blocking deque must be configured before starting listener "
                    + queueName);
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
        startListener();
        running = true;
    }

    @Override
    public synchronized void stop() {
        stop = true;
        if (listenerThread != null) {
            listenerThread.interrupt();
            listenerThread = null;
        }
        if (threadPool != null) {
            threadPool.shutdownNow();
        }
        running = false;
    }


    private void startListener() {
        listenerThread = new QueueThreadFactory(queueName() + "-listener-").newThread(() -> {
            while (!stop) {
                Object message = null;
                try {
                    message = distinationQueue.take();
                    if (message != null) {
                        if (listener == null) {
                            final Object currentMessage = message;
                            threadPool.submit(() -> handleMethodMessage(currentMessage));
                        } else {
                            final Object currentMessage = message;
                            threadPool.submit(() -> handleMessage(currentMessage));
                        }
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    if (!stop) {
                        handleError(message, e);
                    }
                    break;
                } catch (Throwable e) {
                    if (!stop) {
                        handleError(message, e);
                    }
                }
            }
        });
        listenerThread.start();
    }

    private void handleMessage(Object message) {
        try {
            MDC.put("UUID", UUID.randomUUID().toString());
            listener.onMessage(message);
        } catch (Throwable e) {
            handleError(message, e);
        } finally {
            MDC.remove("UUID");
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

    public RBlockingDeque<Object> getDistinationQueue() {
        return distinationQueue;
    }

    public void setDistinationQueue(RBlockingDeque<Object> distinationQueue) {
        this.distinationQueue = distinationQueue;
    }

    public DelayQueueListener getListener() {
        return listener;
    }

    public void setListener(DelayQueueListener listener) {
        this.listener = listener;
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
