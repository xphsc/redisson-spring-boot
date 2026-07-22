package com.xphsc.example.lock;

import java.util.concurrent.CompletionStage;

public interface ExampleLockService {

    String execute(String orderId, long holdMillis);

    String executeWithFixedLease(String orderId, long holdMillis);

    CompletionStage<String> executeAsync(String orderId, long holdMillis);
}
