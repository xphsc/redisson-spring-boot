<img src="image/logo.png" width="40%" height="40%" />

# redisson-spring-boot 低耦合集成的高度扩展组件
[![Build Status](https://api.travis-ci.org/cn.xphsc.boot/redisson-spring-boot-starter.svg?branch=master)]
[![JDK](https://img.shields.io/badge/JDK-1.8+-green.svg)]
[![Maven central](https://img.shields.io/maven-central/v/cn.xphsc.boot/redisson-spring-boot-starter.svg))]
[![APACHE 2 License](https://img.shields.io/badge/license-Apache2-blue.svg?style=flat)](LICENSE)

#### 介绍
**redisson-spring-boot低耦合集成的高度扩展组件**
redisson  Spring Boot低耦合集成的高度扩展组件
* redisson 操作以及限流 分布式锁 延迟消息队列，消息队列,布隆过滤器
#### 安装教程
~~~
<dependency>
  <groupId>cn.xphsc.boot</groupId>
  <artifactId>redisson-spring-boot-starter</artifactId>
     <version>1.0.4</version>
</dependency>

~~~

#### 3使用说明
yaml(和spring data redis基本一样)
~~~
spring:
 redis:
  host: localhost
  password: xxx
  database: 2
~~~
####配置说明
参数 |说明
---|---
host | url地址
port | 端口
password | 密码
database |database
ClusterServer | 集群
codec | codec
connectionMinimumIdleSize | 最小空闲连接数,默认值:10,最小保持连接数（长连接）
idleConnectionTimeout | 而连接空闲时间超过了该数值，这些连接将会自动被关闭，并从连接池里去掉
pingTimeout | ping节点超时,单位：毫秒,默认1000
connectTimeout | 连接等待超时,单位：毫秒,默认10000
timeout | 命令等待超时,单位：毫秒,默认3000；等待节点回复命令的时间。该时间从命令发送成功时开始计时
retryAttempts| 命令失败重试次数，默认值:3
retryInterval|命令重试发送时间间隔，单位：毫秒,默认值:1500
reconnectionTimeout|重新连接时间间隔，单位：毫秒,默认值：3000;连接断开时，等待与其重新建立连接的时间间隔
failedAttempts|执行失败最大次数, 默认值：3；失败后直到 reconnectionTimeout超时以后再次尝试。
subscriptionsPerConnection|单个连接最大订阅数量，默认值：5
clientName|客户端名称
subscriptionConnectionMinimumIdleSize|长期保持一定数量的发布订阅连接是必须的、
connectionPoolSize|发布和订阅连接池大小，默认值：50
dnsMonitoring|是否启用DNS监测，默认值：false
dnsMonitoringInterval|DNS监测时间间隔，单位：毫秒，默认值：5000

3.3.1限流
~~~
   public class TestController {
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

       @RateLimit(rate = 5, rateInterval = "10s",keys = {"#user.name","#user.id"})
      *  public String hello(User user) { }
~~~
  3.3.2分布式锁
~~~
 @RedissonLock(name = "test-lock", waitTime = 2, lockTimeoutStrategy = LockTimeoutStrategy.FAIL_FAST)
    @Override
    public void test(@RedissonLockKey String id) {
        this.testLock(id, user);
    }
     @RedissonLock(name = "test-lock", keys = {"#id"}, leaseTime=-1)
      public void testLock(String id){}
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


