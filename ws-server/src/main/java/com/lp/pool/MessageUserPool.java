package com.lp.pool;


import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.*;

/**
 * 用户发送消息线程池
 *
 * @author Administrator
 */
@Slf4j
public class MessageUserPool {
    private static final Map<Long, MessageUserPool> PUSH_USER_POOL_MAP = new ConcurrentHashMap<>();
    //线程池个数 虚拟线程多点无所谓
    private static final Long PROCESS_POOL_SIZE = 5000L;
    private ExecutorService executors = null;

    /**
     * 线程初始化
     */
    private static void initDeskProcessPoolMap() {
        for (long i = 0; i < PROCESS_POOL_SIZE; i++) {
            MessageUserPool pool = new MessageUserPool();
            if (pool.executors == null) {
                pool.executors = new ThreadPoolExecutor(1, 1, 0L,
                        TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>(), Thread.ofVirtual().name("MessageUserPool-", i).factory());
            }
            PUSH_USER_POOL_MAP.put(i, pool);
        }
    }

    /**
     * 获取用户线程池
     *
     * @param userId
     * @return
     */
    public static MessageUserPool getMessageUserPool(Long userId) {
        if (PUSH_USER_POOL_MAP.isEmpty()) {
            synchronized (MessageUserPool.class) {
                if (PUSH_USER_POOL_MAP.isEmpty()) {
                    initDeskProcessPoolMap();
                }
            }
        }
        return PUSH_USER_POOL_MAP.get(userId % PROCESS_POOL_SIZE);
    }


    public void execute(Runnable runnable) {
        try {
            executors.execute(runnable);
        } catch (Exception e) {
            log.error("用户webSocket发送消息线程执行执行失败", e);
        }
    }
}
