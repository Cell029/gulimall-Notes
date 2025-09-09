package com.project.gulimall.seckill.scheduled;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@EnableScheduling
public class HelloSchedule {

    /*@Scheduled(fixedRate = 1000)
    public void hello() {
        log.info("hello");
    }*/

    @Scheduled(fixedRate = 1000)
    public void task1() throws InterruptedException {
        System.out.println("Task1开始: " + Thread.currentThread().getName());
        Thread.sleep(3000); // 模拟耗时操作
        System.out.println("Task1结束");
    }

    @Async
    @Scheduled(fixedRate = 1000)
    public void task2() {
        System.out.println("Task2执行: " + Thread.currentThread().getName());
    }
}
