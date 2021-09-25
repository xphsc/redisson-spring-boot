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

import cn.xphsc.redisson.core.RedissonTemplate;
import io.netty.channel.nio.NioEventLoopGroup;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.client.codec.Codec;
import org.redisson.config.Config;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

/**
 * {@link }
 * @author <a href="xiongpeih@163.com">huipei.x</a>
 * @description:
 * @since 1.0.0
 */
@Configuration
@AutoConfigureAfter(RedisAutoConfiguration.class)
@EnableConfigurationProperties(RedissonProperties.class)
@ConditionalOnProperty(prefix = RedissonProperties.PREFIX,
        name = "enabled",
        matchIfMissing=true,
        havingValue = "true")
public class RedissonAutoConfiguration {
    private RedissonProperties redissonProperties;

    public RedissonAutoConfiguration(RedissonProperties redissonProperties){
        this.redissonProperties= redissonProperties;
    }

    @Bean(destroyMethod = "shutdown")
    @ConditionalOnMissingBean
    public  RedissonClient redisson() throws Exception {
        Config config = new Config();
        if (redissonProperties.getCluster() != null) {
            config.useClusterServers().setPassword(redissonProperties.getPassword())
                    .addNodeAddress(redissonProperties.getCluster().getNodes());
        }
        else if (StringUtils.hasText(redissonProperties.getUrl())) {
            config.useSingleServer().setAddress(redissonProperties.getUrl())
                    .setDatabase(redissonProperties.getDatabase())
                    .setPassword(redissonProperties.getPassword());
        }
        else {
            config.useSingleServer().setAddress(redissonProperties.getHost() + ":" + redissonProperties.getPort())
                    .setDatabase(redissonProperties.getDatabase())
                    .setPassword(redissonProperties.getPassword());
        }

 if(StringUtils.hasText(redissonProperties.getClientName())){
     config.useSingleServer().setClientName(redissonProperties.getClientName());
 }
        if(redissonProperties.getSubscriptionConnectionMinimumIdleSize()!=0){
            config.useSingleServer().setSubscriptionConnectionMinimumIdleSize(redissonProperties.getSubscriptionConnectionMinimumIdleSize());
        }

        config.useSingleServer().setConnectionMinimumIdleSize(redissonProperties.getConnectionMinimumIdleSize())
                .setConnectionPoolSize(redissonProperties.getConnectionPoolSize())
                .setDnsMonitoring(redissonProperties.isDnsMonitoring())
                .setDnsMonitoringInterval(redissonProperties.getDnsMonitoringInterval())
                .setSubscriptionConnectionPoolSize(redissonProperties.getSubscriptionConnectionPoolSize())
                .setSubscriptionsPerConnection(redissonProperties.getSubscriptionsPerConnection())
                .setFailedAttempts(redissonProperties.getFailedAttempts())
                .setRetryAttempts(redissonProperties.getRetryAttempts())
                .setRetryInterval(redissonProperties.getRetryInterval())
                .setReconnectionTimeout(redissonProperties.getReconnectionTimeout())
                .setTimeout(redissonProperties.getTimeout())
                .setConnectTimeout(redissonProperties.getConnectTimeout())
                .setIdleConnectionTimeout(redissonProperties.getIdleConnectionTimeout())
                .setPingTimeout(redissonProperties.getPingTimeout());
        Codec codec = (Codec) ClassUtils.forName(redissonProperties.getCodec(), ClassUtils.getDefaultClassLoader()).newInstance();
        config.setCodec(codec);
        config.setEventLoopGroup(new NioEventLoopGroup());
        return Redisson.create(config);
    }


    @Bean
    public RedissonTemplate redissonTemplate(RedissonClient redissonClient) {
        return new RedissonTemplate(redissonClient);
    }
}

