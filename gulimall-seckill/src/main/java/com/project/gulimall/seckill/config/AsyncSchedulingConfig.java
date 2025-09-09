package com.project.gulimall.seckill.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

@Configuration
public class AsyncSchedulingConfig implements SchedulingConfigurer {

    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
        ThreadPoolTaskScheduler taskScheduler = new ThreadPoolTaskScheduler();
        taskScheduler.setPoolSize(5); // 设置线程池大小
        taskScheduler.setThreadNamePrefix("scheduled-task-");
        taskScheduler.setAwaitTerminationSeconds(60);
        taskScheduler.setWaitForTasksToCompleteOnShutdown(true);
        taskScheduler.initialize();
        taskRegistrar.setTaskScheduler(taskScheduler);
    }
}
