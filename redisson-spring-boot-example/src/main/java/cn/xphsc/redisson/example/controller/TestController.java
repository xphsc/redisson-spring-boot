package cn.xphsc.redisson.example.controller;



import cn.xphsc.redisson.core.delayqueue.DelayQueueTemplate;
import cn.xphsc.redisson.example.domain.DeptVO;
import cn.xphsc.redisson.example.domain.UserVO;
import cn.xphsc.redisson.example.queue.DelayMessage;
import cn.xphsc.redisson.example.service.TestService;
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

    @GetMapping
    public String test(String id) {
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
        delayQueueTemplate.send("delay-message-queue-name", delayMessage, 10, TimeUnit.SECONDS);

        return delayMessage;
    }



}
