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


import org.springframework.boot.context.properties.ConfigurationProperties;


/**
 * {@link }
 * @author <a href="xiongpeih@163.com">huipei.x</a>
 * @description:
 * @since 1.0.0
 */
@ConfigurationProperties(prefix = RedissonProperties.PREFIX)
public class RedissonProperties {
    public static final String PREFIX = "spring.redis";

    private String url;
    private String host = "localhost";
    private Integer port = 6379;
    private String password;
    private int database = 0;
    private ClusterServer cluster;

    private String codec = "org.redisson.codec.JsonJacksonCodec";

    //最小空闲连接数,默认值:10,最小保持连接数（长连接）
    private int connectionMinimumIdleSize=10;
    //连接空闲超时，单位：毫秒 默认10000;当前连接池里的连接数量超过了最小空闲连接数，
    //而连接空闲时间超过了该数值，这些连接将会自动被关闭，并从连接池里去掉
    private int idleConnectionTimeout=10000;
    //ping节点超时,单位：毫秒,默认1000
    private int pingTimeout=1000;
    //连接等待超时,单位：毫秒,默认10000
    private int connectTimeout=10000;
    //命令等待超时,单位：毫秒,默认3000；等待节点回复命令的时间。该时间从命令发送成功时开始计时
    private int timeout=3000;
    //命令失败重试次数，默认值:3
    private int retryAttempts=3;
    //命令重试发送时间间隔，单位：毫秒,默认值:1500
    private int retryInterval=1500;
    //重新连接时间间隔，单位：毫秒,默认值：3000;连接断开时，等待与其重新建立连接的时间间隔
    private int reconnectionTimeout=3000;
    //执行失败最大次数, 默认值：3；失败后直到 reconnectionTimeout超时以后再次尝试。
    private int failedAttempts=3;
    //单个连接最大订阅数量，默认值：5
    private int subscriptionsPerConnection=5;
    //客户端名称
    private String clientName;
    //长期保持一定数量的发布订阅连接是必须的
    private int subscriptionConnectionMinimumIdleSize;
    //发布和订阅连接池大小，默认值：50
    private int subscriptionConnectionPoolSize=50;
    //发布和订阅连接池大小，默认值：50
    private int connectionPoolSize=50;
    //是否启用DNS监测，默认值：false
    private boolean dnsMonitoring;
    //DNS监测时间间隔，单位：毫秒，默认值：5000
    private int dnsMonitoringInterval=5000;



    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getDatabase() {
        return database;
    }

    public void setDatabase(int database) {
        this.database = database;
    }

    public ClusterServer getCluster() {
        return cluster;
    }

    public void setCluster(ClusterServer cluster) {
        this.cluster = cluster;
    }

    public String getCodec() {
        return codec;
    }

    public void setCodec(String codec) {
        this.codec = codec;
    }

    public int getConnectionMinimumIdleSize() {
        return connectionMinimumIdleSize;
    }

    public void setConnectionMinimumIdleSize(int connectionMinimumIdleSize) {
        this.connectionMinimumIdleSize = connectionMinimumIdleSize;
    }

    public int getIdleConnectionTimeout() {
        return idleConnectionTimeout;
    }

    public void setIdleConnectionTimeout(int idleConnectionTimeout) {
        this.idleConnectionTimeout = idleConnectionTimeout;
    }

    public int getPingTimeout() {
        return pingTimeout;
    }

    public void setPingTimeout(int pingTimeout) {
        this.pingTimeout = pingTimeout;
    }

    public int getConnectTimeout() {
        return connectTimeout;
    }

    public void setConnectTimeout(int connectTimeout) {
        this.connectTimeout = connectTimeout;
    }

    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    public int getRetryAttempts() {
        return retryAttempts;
    }

    public void setRetryAttempts(int retryAttempts) {
        this.retryAttempts = retryAttempts;
    }

    public int getRetryInterval() {
        return retryInterval;
    }

    public void setRetryInterval(int retryInterval) {
        this.retryInterval = retryInterval;
    }

    public int getReconnectionTimeout() {
        return reconnectionTimeout;
    }

    public void setReconnectionTimeout(int reconnectionTimeout) {
        this.reconnectionTimeout = reconnectionTimeout;
    }

    public int getFailedAttempts() {
        return failedAttempts;
    }

    public void setFailedAttempts(int failedAttempts) {
        this.failedAttempts = failedAttempts;
    }

    public int getSubscriptionsPerConnection() {
        return subscriptionsPerConnection;
    }

    public void setSubscriptionsPerConnection(int subscriptionsPerConnection) {
        this.subscriptionsPerConnection = subscriptionsPerConnection;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public int getSubscriptionConnectionMinimumIdleSize() {
        return subscriptionConnectionMinimumIdleSize;
    }

    public void setSubscriptionConnectionMinimumIdleSize(int subscriptionConnectionMinimumIdleSize) {
        this.subscriptionConnectionMinimumIdleSize = subscriptionConnectionMinimumIdleSize;
    }

    public int getSubscriptionConnectionPoolSize() {
        return subscriptionConnectionPoolSize;
    }

    public void setSubscriptionConnectionPoolSize(int subscriptionConnectionPoolSize) {
        this.subscriptionConnectionPoolSize = subscriptionConnectionPoolSize;
    }

    public int getConnectionPoolSize() {
        return connectionPoolSize;
    }

    public void setConnectionPoolSize(int connectionPoolSize) {
        this.connectionPoolSize = connectionPoolSize;
    }

    public boolean isDnsMonitoring() {
        return dnsMonitoring;
    }

    public void setDnsMonitoring(boolean dnsMonitoring) {
        this.dnsMonitoring = dnsMonitoring;
    }

    public int getDnsMonitoringInterval() {
        return dnsMonitoringInterval;
    }

    public void setDnsMonitoringInterval(int dnsMonitoringInterval) {
        this.dnsMonitoringInterval = dnsMonitoringInterval;
    }


    public static class ClusterServer {
        private String[] nodes;

        public String[] getNodes() {
            return nodes;
        }

        public void setNodes(String[] nodes) {
            this.nodes = nodes;
        }
    }
}
