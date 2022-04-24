package com.xphsc.test.queue;

import cn.xphsc.redisson.core.delayqueue.DelayQueueTemplate;
import cn.xphsc.redisson.core.queue.RedissonQueueTemplate;
import com.alibaba.fastjson.JSON;
import com.xphsc.test.ExampleApplication;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.concurrent.TimeUnit;

/**
 * {@link }
 *
 * @author <a href="xiongpeih@163.com">huipei.x</a>
 * @description:
 * @since 1.0.0
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = ExampleApplication.class)
public class QueueTest {

    @Autowired
    private DelayQueueTemplate delayQueueTemplate;
    @Autowired
    private RedissonQueueTemplate queueTemplate;
    @Test
    public void test(){
        DelayMessage delayMessage=new DelayMessage();
        delayMessage.setName("12");
        delayQueueTemplate.send("delay-message-queue-name", delayMessage, 1, TimeUnit.SECONDS);
    }

    @Test
    public void test1(){
        DelayMessage delayMessage=new DelayMessage();
        delayMessage.setName("---222");
        delayQueueTemplate.send("delay1-message-queue-name", delayMessage, 1, TimeUnit.SECONDS);
    }

    @Test
    public void test2(){
        DelayMessage delayMessage=new DelayMessage();
        delayMessage.setName("---re5rt");
        queueTemplate.send("delay333-message-queue-name", delayMessage);

    }
    @Test
    public void test3(){
        DelayMessage delayMessage=new DelayMessage();
        delayMessage.setName("---test3");
        queueTemplate.send("delay-2-2-message-queue-name", JSON.toJSONString(delayMessage));

    }

    @Test
    public void test333(){
        DelayMessage delayMessage=new DelayMessage();
        delayMessage.setName("---message333");
        queueTemplate.send("message333-message-queue-name", delayMessage);

    }

    @Test
    public void test43(){
        DelayMessage delayMessage=new DelayMessage();
        delayMessage.setName("---message444");
        queueTemplate.send("message444-message-queue-name", delayMessage);

    }

}
