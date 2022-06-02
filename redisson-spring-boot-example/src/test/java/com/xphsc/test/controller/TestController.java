package com.xphsc.test.controller;



//import cn.xphsc.redisson.annotation.RedissonDelayQueryListener;
import cn.xphsc.redisson.core.RedissonTemplate;
import cn.xphsc.redisson.core.delayqueue.DelayQueueTemplate;
import cn.xphsc.redisson.core.queue.RedissonQueueTemplate;
import com.xphsc.test.domain.DeptVO;
import com.xphsc.test.domain.UserVO;
import com.xphsc.test.queue.DelayMessage;
import com.xphsc.test.service.TestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/api")
public class TestController {

    @Autowired
    private TestService testService;
    @Autowired
    RedissonQueueTemplate redissonQueueTemplate;
    @GetMapping
    public String test(String id) {
        //   Distributed-lock
//        id = new Random().nextInt(100) + "";
        testService.testLock(id, new UserVO(1L, "zhangsan", 18, new DeptVO(1L, "交易平台")));
        return "ok";
    }

    @GetMapping("/test2")
    public String test2(String id) {
//        id = new Random().nextInt(100) + "";
        testService.testLock2(id, new UserVO(1L, "zhangsan", 18, new DeptVO(1L, "交易平台")));
        return "ok";
    }

    @GetMapping("/test3")
    public String test3(String id) {
//        id = new Random().nextInt(100) + "";
        testService.testLock3(id, new UserVO(1L, "zhangsan", 18, new DeptVO(1L, "交易平台")));
        return "ok";
    }

    @GetMapping("/test4")
    public String test4(String id) {
//        id = new Random().nextInt(100) + "";
        testService.testLock4(id, new UserVO(1L, "zhangsan", 18, new DeptVO(1L, "交易平台")));
        return "ok";
    }
    @Autowired
    private DelayQueueTemplate delayQueueTemplate;
    @GetMapping("/test5")
    public Object test(){
        DelayMessage delayMessage=new DelayMessage();
        delayMessage.setName("2");
        DelayMessage delayMessage1= (DelayMessage)  delayQueueTemplate.receive("delay-2-2-message-queue-name");
        return delayMessage;
    }



    @GetMapping("/test7")
    public Object test7(){
        DelayMessage delayMessage=new DelayMessage();
        delayMessage.setName("12");
        delayQueueTemplate.send("delay-message-queue-name", delayMessage, 1, TimeUnit.MINUTES);
        return delayMessage;
    }

}}
