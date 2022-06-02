# redisson-spring-boot 低耦合集成的高度扩展组件
[![Build Status](https://api.travis-ci.org/cn.xphsc.boot/redisson-spring-boot-starter.svg?branch=master)]
[![JDK](https://img.shields.io/badge/JDK-1.8+-green.svg)]
[![Maven central](https://maven-badges.herokuapp.com/maven-central/cn.xphsc.boot/redisson-spring-boot-starter/badge.svg)]
[![APACHE 2 License](https://img.shields.io/badge/license-Apache2-blue.svg?style=flat)](LICENSE)

#### 介绍
{**redisson-spring-boot低耦合集成的高度扩展组件**
redisson  Spring Boot低耦合集成的高度扩展组件
redisson 操作以及限流 分布式锁 延迟消息队列，消息队列
#### 安装教程
~~~
<dependency>
  <groupId>cn.xphsc.boot</groupId>
  <artifactId>redisson-spring-boot-starter</artifactId>
     <version>1.0.2</version>
</dependency>

~~~

#### 3使用说明

3.3.1限流
~~~
 public class Test1Controller {
     @GetMapping("/get")
     @RateLimit(rate = 1, rateInterval = "10s")
     public String get() {
         return "get";
     }

     @GetMapping("/get1")
     @RateLimit(rate = 2, rateInterval = "10s",rateExpression = "${spring.ratelimiter.max:2}")
     public String get1() {
         return "get";
     }
     @GetMapping("/get2")
     @RateLimit(rate = 1, rateInterval = "2s")
     public String get2( @RateLimitKey(value = "#name") String name) {
         return "get";
     }
~~~
  3.3.2分布式锁
~~~
 @RedissonLock(name = "test-lock", waitTime = 2, lockTimeoutStrategy = LockTimeoutStrategy.FAIL_FAST)
    @Override
    public void test(@RedissonLockKey String id) {
        this.testLock(id, user);
    }
~~~
  3.3.3 延迟队列
~~~
@Component
public class MyDelayQueueListener {
  @RedissonDelayQueueListener(queueName = "delay-message-queue-name")
    public void onMessage(DelayMessage delayMessage) {
        System.out.println("--delayMessage---"+delayMessage.getName());
    }
    @RedissonDelayQueueListener(queueName = "delay1-message-queue-name")
    public void onMessage1(DelayMessage delayMessage) {
        System.out.println("---delayMessage1----"+delayMessage.getName());
    }

}
  @Autowired
    private DelayQueueTemplate delayQueueTemplate;
    @Test
    public void test(){
        DelayMessage delayMessage=new DelayMessage();
        delayMessage.setName("小王");
        delayQueueTemplate.send("delay-message-queue-name", delayMessage, 1, TimeUnit.SECONDS);
    }

    @Test
    public void test1(){
        DelayMessage delayMessage=new DelayMessage();
        delayMessage.setName("小熊");
        delayQueueTemplate.send("delay1-message-queue-name", delayMessage, 1, TimeUnit.SECONDS);
    }
~~~
  3.3.4 消息队列
~~~
@Component
public class MyQueueListener {
 @RedissonQueueListener(queueName = "message-message-queue-name")
    public void onMessage(Message message) {
        System.out.println("---message--------"+message.getName());
    }
   @RedissonQueueListener(queueName = "message1-message-queue-name")
    public void onMessage1(Message message) {
        System.out.println("message1------"+message.getName());
    }

}

    @Autowired
    private RedissonQueueTemplate queueTemplate;

    @Test
    public void test(){
        Message message=new Message();
        message.setName("---message------");
        queueTemplate.send("message-message-queue-name", message);

    }

~~~


