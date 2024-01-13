package org.example.config;

import org.redisson.api.RBlockingDeque;
import org.redisson.api.RDelayedQueue;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Author sunmengjin
 * @Create RefundNumDelayConfig.java 2024/1/5 15:40
 * @Description: 延迟退号配置
 *
 */
@Configuration
public class RefundNumDelayConfig {
    private static final String REFUND_NUM_QUEUE = "REFUND_NUM_QUEUE";
    @Autowired
    private RedissonClient redissonClient;

    @Bean("refundBlockQueue")
    public RBlockingDeque<String> getBlockQueue() {
        return redissonClient.getBlockingDeque(REFUND_NUM_QUEUE);
    }


    @Bean("refundDelayedQueue")
    public RDelayedQueue<String> getDelayQueue() {
        RBlockingDeque<String> blockingDeque = redissonClient.getBlockingDeque(REFUND_NUM_QUEUE);
        return redissonClient.getDelayedQueue(blockingDeque);
    }
}
