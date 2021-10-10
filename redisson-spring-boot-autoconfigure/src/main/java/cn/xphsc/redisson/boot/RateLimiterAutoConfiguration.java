/*
 * Copyright (c) 2021 huipei.x
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
package cn.xphsc.redisson.boot;

import cn.xphsc.redisson.core.ratelimiter.RatelimitProvider;
import cn.xphsc.redisson.core.ratelimiter.RateLimiterHander;;
//import cn.xphsc.redisson.core.ratelimiter.advice.RateLimitAdviceHandler;
import cn.xphsc.redisson.core.ratelimiter.advice.RateLimitRegistrar;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.Ordered;

/**
 * {@link }
 * @author <a href="xiongpeih@163.com">huipei.x</a>
 * @description:
 * @since 1.0.0
 */
@Configuration
@ConditionalOnClass({RateLimiterHander.class,RatelimitProvider.class })
@AutoConfigureAfter({RedisAutoConfiguration.class})
@ConditionalOnProperty(prefix = RedissonProperties.PREFIX+"ratelimit",
        name = "enabled",
        matchIfMissing=true,
        havingValue = "true")
@Import({RateLimitRegistrar.class})
public class RateLimiterAutoConfiguration implements Ordered {

    @Bean
    @ConditionalOnMissingBean(RateLimiterHander.class)
    public RateLimiterHander rateLimiterHander() {
        return new RateLimiterHander(ratelimitProvider());
    }

    @Bean
    @ConditionalOnMissingBean(RatelimitProvider.class)
    public RatelimitProvider ratelimitProvider() {
        return new RatelimitProvider();
    }

    @Override
    public int getOrder() {
        return Integer.MAX_VALUE-10;
    }
}
