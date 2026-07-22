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
package cn.xphsc.redisson.boot.autoconfigure;

import cn.xphsc.redisson.boot.RedissonProperties;
import cn.xphsc.redisson.core.RedissonTemplate;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.client.codec.Codec;
import org.redisson.config.BaseConfig;
import org.redisson.config.ClusterServersConfig;
import org.redisson.config.Config;
import org.redisson.config.SingleServerConfig;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
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
@ConditionalOnClass({Redisson.class, RedissonClient.class})
@AutoConfigureAfter(RedisAutoConfiguration.class)
@EnableConfigurationProperties(RedissonProperties.class)
@ConditionalOnProperty(prefix = RedissonProperties.PREFIX,
        name = "enabled",
        matchIfMissing=true,
        havingValue = "true")
public class RedissonAutoConfiguration {
    private final RedissonProperties redissonProperties;

    public RedissonAutoConfiguration(RedissonProperties redissonProperties){
        this.redissonProperties= redissonProperties;
    }

    @Bean(destroyMethod = "shutdown")
    @ConditionalOnMissingBean
    public  RedissonClient redisson() throws Exception {
        return Redisson.create(buildConfig());
    }

    Config buildConfig() throws Exception {
        Config config = new Config();
        if (redissonProperties.getCluster() != null) {
            configureCluster(config);
        } else {
            configureSingleServer(config);
        }

        Class<?> codecClass = ClassUtils.forName(redissonProperties.getCodec(), ClassUtils.getDefaultClassLoader());
        if (!Codec.class.isAssignableFrom(codecClass)) {
            throw new IllegalArgumentException("Configured codec must implement " + Codec.class.getName());
        }
        config.setCodec((Codec) codecClass.getDeclaredConstructor().newInstance());
        return config;
    }

    private void configureSingleServer(Config config) {
        String address = StringUtils.hasText(redissonProperties.getUrl())
                ? normalizeAddress(redissonProperties.getUrl())
                : normalizeAddress(redissonProperties.getHost() + ":" + redissonProperties.getPort());
        SingleServerConfig serverConfig = config.useSingleServer()
                .setAddress(address)
                .setDatabase(redissonProperties.getDatabase())
                .setConnectionMinimumIdleSize(redissonProperties.getConnectionMinimumIdleSize())
                .setConnectionPoolSize(redissonProperties.getConnectionPoolSize())
                .setDnsMonitoringInterval(redissonProperties.getDnsMonitoringInterval())
                .setSubscriptionConnectionPoolSize(redissonProperties.getSubscriptionConnectionPoolSize());
        if (redissonProperties.getSubscriptionConnectionMinimumIdleSize() > 0) {
            serverConfig.setSubscriptionConnectionMinimumIdleSize(
                    redissonProperties.getSubscriptionConnectionMinimumIdleSize());
        }
        configureCommon(serverConfig);
    }

    private void configureCluster(Config config) {
        String[] nodes = redissonProperties.getCluster().getNodes();
        if (nodes == null || nodes.length == 0) {
            throw new IllegalArgumentException("spring.redis.cluster.nodes must not be empty");
        }
        String[] normalizedNodes = new String[nodes.length];
        for (int i = 0; i < nodes.length; i++) {
            normalizedNodes[i] = normalizeAddress(nodes[i]);
        }

        ClusterServersConfig serverConfig = config.useClusterServers()
                .addNodeAddress(normalizedNodes)
                .setMasterConnectionMinimumIdleSize(redissonProperties.getConnectionMinimumIdleSize())
                .setSlaveConnectionMinimumIdleSize(redissonProperties.getConnectionMinimumIdleSize())
                .setMasterConnectionPoolSize(redissonProperties.getConnectionPoolSize())
                .setSlaveConnectionPoolSize(redissonProperties.getConnectionPoolSize())
                .setSubscriptionConnectionPoolSize(redissonProperties.getSubscriptionConnectionPoolSize())
                .setDnsMonitoringInterval(redissonProperties.getDnsMonitoringInterval());
        if (redissonProperties.getSubscriptionConnectionMinimumIdleSize() > 0) {
            serverConfig.setSubscriptionConnectionMinimumIdleSize(
                    redissonProperties.getSubscriptionConnectionMinimumIdleSize());
        }
        configureCommon(serverConfig);
    }

    private void configureCommon(BaseConfig<?> serverConfig) {
        if (StringUtils.hasText(redissonProperties.getPassword())) {
            serverConfig.setPassword(redissonProperties.getPassword());
        }
        if (StringUtils.hasText(redissonProperties.getClientName())) {
            serverConfig.setClientName(redissonProperties.getClientName());
        }
        serverConfig.setSubscriptionsPerConnection(redissonProperties.getSubscriptionsPerConnection())
                .setRetryAttempts(redissonProperties.getRetryAttempts())
                .setRetryInterval(redissonProperties.getRetryInterval())
                .setTimeout(redissonProperties.getTimeout())
                .setConnectTimeout(redissonProperties.getConnectTimeout())
                .setIdleConnectionTimeout(redissonProperties.getIdleConnectionTimeout());
    }

    private String normalizeAddress(String address) {
        if (!StringUtils.hasText(address)) {
            throw new IllegalArgumentException("Redis address must not be empty");
        }
        String value = address.trim();
        if (value.startsWith("redis://") || value.startsWith("rediss://")) {
            return value;
        }
        return RedissonProperties.rediss_prefix + value;
    }


    @Bean
    @ConditionalOnMissingBean
    public RedissonTemplate redissonTemplate(RedissonClient redissonClient) {
        return new RedissonTemplate(redissonClient);
    }
}

