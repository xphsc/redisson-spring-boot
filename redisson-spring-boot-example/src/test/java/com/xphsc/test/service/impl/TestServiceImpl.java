package com.xphsc.test.service.impl;



import cn.xphsc.redisson.annotation.RedissonLock;
import cn.xphsc.redisson.annotation.RedissonLockKey;
import cn.xphsc.redisson.core.distributedlock.entity.LockTimeoutStrategy;
import com.xphsc.test.domain.UserVO;
import com.xphsc.test.service.TestService;

import org.springframework.stereotype.Service;


@Service
public class TestServiceImpl implements TestService {


    @RedissonLock(name = "test-lock",/* keys = {"#id"},*/ leaseTime = -1)
    @Override
    public void testLock(String id, UserVO user) {
       try {
           Thread.sleep(30000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("test-lock finished");
    }

    /**
     * 此方法的锁 与 上面 testLock方法的锁的key是同一个
     * @param id
     * @param user
     */
    @RedissonLock(name = "test-lock", waitTime = 2, lockTimeoutStrategy = LockTimeoutStrategy.FAIL_FAST)
    @Override
    public void testLock2(@RedissonLockKey String id, UserVO user) {
        this.testLock(id, user);
    }

    @RedissonLock(name = "test-lock3")
    @Override
    public void testLock3(String id, @RedissonLockKey("deptVO.id") UserVO user) {
        this.testLock(id, user);
    }

    @RedissonLock(name = "test-lock4")
    @Override
    public void testLock4(String id, @RedissonLockKey("id") UserVO user) {
        this.testLock(id, user);
    }
}
