/*
 * Copyright (c) 2026 huipei.x
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cn.xphsc.redisson.core.queue;

/**
 * Handles listener failures after a message has been received.
 *
 * <p>Applications can implement this interface to add retry, dead-letter,
 * alerting, or application-specific logging behavior.</p>
 */
@FunctionalInterface
public interface RedisListenerErrorHandler {

    void handle(String listenerName, Object message, Throwable error);
}
