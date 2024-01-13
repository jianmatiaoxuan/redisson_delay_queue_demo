package org.example.service;

import com.alibaba.fastjson.JSONObject;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.example.vo.ReturnNumVO;
import org.redisson.RedissonShutdownException;
import org.redisson.api.RBlockingQueue;
import org.redisson.api.RDelayedQueue;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @Author sunmengjin
 * @Create RedissonDelayQueue.java 2024/1/5 15:53
 * @Description:
 */
@Component
@Slf4j
public class RedissonDelayQueue {
    @Autowired
    @Qualifier("refundNumThreadPoolTaskExecutor")
    private ThreadPoolTaskExecutor refundNumThreadPoolTaskExecutor;
    @Autowired
    @Qualifier("refundNumOfferThreadPoolTaskExecutor")
    private ThreadPoolTaskExecutor refundNumOfferThreadPoolTaskExecutor;
    @Autowired
    @Qualifier("refundDelayedQueue")
    private RDelayedQueue<String> delayQueue;
    @Autowired
    @Qualifier("refundBlockQueue")
    private RBlockingQueue<String> blockingQueue;
    @Autowired
    private AppointmentService appointmentService;
    @Autowired
    private RedissonClient redissonClient;

    @PostConstruct
    public void init() {
        startDelayQueueConsumer();
    }

    @PreDestroy
    public void shutdown() {
        log.info("Starting shutdown process...");
        // 关闭线程池
        ExecutorService executorService1 = refundNumThreadPoolTaskExecutor.getThreadPoolExecutor();
        ExecutorService executorService2 = refundNumOfferThreadPoolTaskExecutor.getThreadPoolExecutor();
        log.info("Shutting down refundNumThreadPoolTaskExecutor...");
        executorService1.shutdown();
        executorService2.shutdown();

        // 关闭 Redisson 客户端
        log.info("Shutting down redissonClient...");
        redissonClient.shutdown();

        log.info("Shutdown process completed.");
    }

    private void startDelayQueueConsumer() {
        refundNumThreadPoolTaskExecutor.execute(() -> {
            while (true) {
                try {
                    String task = blockingQueue.take();
                    log.info("接收到退号任务:{}", task);
                    if (StringUtils.isEmpty(task)) {
                        continue;
                    }
                    ReturnNumVO returnNumVO = JSONObject.parseObject(task, ReturnNumVO.class);
                    if (null == returnNumVO || null == returnNumVO.getNumberId() || null == returnNumVO.getStock()) {
                        continue;
                    }
                    appointmentService.returnNum(returnNumVO.getNumberId(), returnNumVO.getStock());
                } catch (RedissonShutdownException se) {
                    log.error("RedissonDelayQueue消费者线程被中断");
                } catch (Exception e) {
                    log.error("RedissonDelayQueue消费者线程异常");
                    e.printStackTrace();
                }
            }
        });
    }

    public void offerTask(String task, long seconds) {
        log.info("添加延迟任务:{} 延迟时间:{}s", task, seconds);
        refundNumOfferThreadPoolTaskExecutor.execute(() -> delayQueue.offer(task, seconds, TimeUnit.SECONDS));
    }

    public void removeTask(String task) {
        log.info("删除延迟任务:{}", task);
        refundNumOfferThreadPoolTaskExecutor.execute(() -> delayQueue.remove(task));
    }
}
