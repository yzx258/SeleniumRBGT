package com.example.selenium.config;

/**
 * @program: rbgt
 * @author: 俞春旺
 * @create: 2020/06/13 22:16 @description： 描述：线程池配置类AsyncTaskConfig
 */

import java.util.UUID;
import java.util.concurrent.ThreadPoolExecutor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
@EnableAsync
public class AsyncTaskConfig {

    @Value(value = "${spring.task.pool.corePoolSize}")
    int corePoolSize;
    @Value(value = "${spring.task.pool.maxPoolSize}")
    int maxPoolSize;
    @Value(value = "${spring.task.pool.queueCapacity}")
    int queueCapacity;
    @Value(value = "${spring.task.pool.keepAliveSeconds}")
    int keepAliveSeconds;

    @Bean
    public ThreadPoolTaskExecutor myTaskAsyncPool() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(corePoolSize);
        executor.setMaxPoolSize(maxPoolSize);
        executor.setQueueCapacity(queueCapacity);
        executor.setKeepAliveSeconds(keepAliveSeconds);
        executor.setAllowCoreThreadTimeOut(true);
        executor.setThreadNamePrefix("MyExecutor-" + UUID.randomUUID());

        // rejection-policy：当pool已经达到max size的时候，如何处理新任务
        // CALLER_RUNS：不在新线程中执行任务，而是由调用者所在的线程来执行
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.initialize();
        return executor;
    }
}