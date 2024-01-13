package org.example.config;

import org.apache.commons.lang3.StringUtils;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.client.codec.StringCodec;
import org.redisson.config.Config;
import org.redisson.config.SentinelServersConfig;
import org.redisson.config.SingleServerConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author sunmengjin
 * @Create RedisConfig.java 2024/1/5 15:34
 * @Description: redisson配置
 */
@Configuration
public class RedisConfig {
    @Autowired
    private RedisProperties redisProperties;

    /**
     * 初始化RedissonClient客户端
     * 支持单机和哨兵模式
     * 注意：
     * 此实例集群为3节点，各节点1主1从
     * 集群模式,集群节点的地址须使用“redis://”前缀，否则将会报错。
     *
     * @return {@link RedissonClient}
     */
    @Bean(destroyMethod = "shutdown")
    public RedissonClient getRedissonClient() {
        Config config = new Config();
        config.setCodec(new StringCodec());
        if (redisProperties.getSentinel() != null) {
            //集群模式配置
            List<String> nodes = redisProperties.getSentinel().getNodes();

            List<String> clusterNodes = new ArrayList<>();
            for (String node : nodes) {
                clusterNodes.add("redis://" + node);
            }
            SentinelServersConfig clusterServersConfig = config.useSentinelServers()
                    .addSentinelAddress(clusterNodes.toArray(new String[clusterNodes.size()]));

            if (StringUtils.isNotEmpty(redisProperties.getPassword())) {
                clusterServersConfig.setPassword(redisProperties.getPassword());
            }
            clusterServersConfig.setMasterName(redisProperties.getSentinel().getMaster());
        } else {
            //单节点配置
            String address = "redis://" + redisProperties.getHost() + ":" + redisProperties.getPort();
            SingleServerConfig serverConfig = config.useSingleServer();
            serverConfig.setAddress(address);
            if (StringUtils.isNotEmpty(redisProperties.getPassword())) {
                serverConfig.setPassword(redisProperties.getPassword());
            }
            serverConfig.setDatabase(redisProperties.getDatabase());
        }
        //看门狗的锁续期时间，默认30000ms，这里配置成15000ms
        config.setLockWatchdogTimeout(15000);
        return Redisson.create(config);
    }
}
