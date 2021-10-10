package com.xphsc.test.example.queue;

import cn.xphsc.redisson.core.delayqueue.DelayQueueTemplate;
import com.xphsc.test.example.ExampleApplication;
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

    @Test
    public void test(){
        DelayMessage delayMessage=new DelayMessage();
        delayMessage.setName("1");
        delayQueueTemplate.send("delay-message-queue-name", delayMessage, 1, TimeUnit.SECONDS);
    }
}
