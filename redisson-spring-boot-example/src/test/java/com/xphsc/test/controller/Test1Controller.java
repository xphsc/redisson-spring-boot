package com.xphsc.test.controller;

import cn.xphsc.redisson.annotation.RateLimit;
import com.xphsc.test.domain.UserVO;
import org.springframework.web.bind.annotation.*;

/**
 * {@link }
 *
 * @author <a href="xiongpeih@163.com">huipei.x</a>
 * @description:
 * @since 1.0.0
 */
@RestController
@RequestMapping("/test")
public class Test1Controller {

    @GetMapping("/get")
    @RateLimit(rate = 1, rateInterval = "10s")
    public String get(String name) {
        return "get";
    }

    @GetMapping("/get2")
    @RateLimit(rate = 2, rateInterval = "10s",rateExpression = "${spring.ratelimiter.max:2}")
    public String get2() {
        return "get";
    }

    /**
     * 提供 wrk 压测工具压测的接口 , 测试脚本: wrk -t16 -c100 -d15s --latency http://localhost:8080/test/wrk
     */
    @GetMapping("/wrk")
    @RateLimit(rate = 100000000, rateInterval = "30s")
    public String wrk() {
        return "get";
    }

    @PostMapping("/hello")
    @RateLimit(rate = 5, rateInterval = "10s",keys = {"#user.name","user.id"})
    public String hello(@RequestBody UserVO user) {
        return "hello";
    }

    public String getFallback(String name){
        return "命中了" + name;
    }

    public String keyFunction(String name) {
        return "keyFunction";
    }
}
