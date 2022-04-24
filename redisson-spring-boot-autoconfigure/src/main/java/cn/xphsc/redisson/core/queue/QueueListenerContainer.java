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
    private RTopic topic;
    private boolean running;
    private ExecutorService threadPool;
    private volatile boolean stop = false;
    private String queueName;
    private List<RedisListenerMethod> redisListenerMethods;
    private ApplicationContext applicationContext;
    @Override
    public void start() {
        threadPool = new ThreadPoolExecutor(1,
                Runtime.getRuntime().availableProcessors(),
                30,
                TimeUnit.MINUTES,
                new SynchronousQueue(),
                new QueueThreadFactory(queueName()),
                new ThreadPoolExecutor.CallerRunsPolicy()
        );
        startListener();
        setRunning(true);
    }


    private void startListener() {
        new Thread(() -> {
                try {
                    topic.addListener(new MessageListener() {
                        @Override
                        public void onMessage(String topic, Object o) {
                            if (o != null) {
                                threadPool.submit(() -> handleMethodMessage(o));
                            }
                        }
                    });
                } catch (Exception e) {
            }
        }).start();
    }

    private void handleMethodMessage(Object message) {
        try {
            MDC.put("UUID", UUID.randomUUID().toString());
            if(!redisListenerMethods.isEmpty()&&redisListenerMethods.size()>0) {
                for (RedisListenerMethod redisListenerMethod : redisListenerMethods) {
                    Method targetMethod = redisListenerMethod.getTargetMethod();
                    targetMethod.invoke(redisListenerMethod.getBean(applicationContext), message);
                }
            }
        } catch (Exception e) {
        }finally {
            MDC.remove("UUID");
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
    public void stop() {
        stop = true;
    }

    @Override
    public boolean isRunning() {
        return running;
    }

    private void setRunning(boolean running) {
        this.running = running;
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

    }



    public int getPhase() {
        return 0;
    }
}
