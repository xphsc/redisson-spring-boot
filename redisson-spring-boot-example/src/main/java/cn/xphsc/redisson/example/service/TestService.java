package cn.xphsc.redisson.example.service;


import com.xphsc.test.example.domain.UserVO;

public interface TestService {

    void testLock(String id, UserVO user);

    void testLock2(String id, UserVO user);

    void testLock3(String id, UserVO user);

    void testLock4(String id, UserVO user);

}
