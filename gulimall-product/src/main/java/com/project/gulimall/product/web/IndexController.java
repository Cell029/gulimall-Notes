package com.project.gulimall.product.web;

import com.project.gulimall.product.domain.entity.CategoryEntity;
import com.project.gulimall.product.domain.vo.Catalog2Vo;
import com.project.gulimall.product.service.CategoryService;
import org.redisson.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Controller
public class IndexController {

    @Autowired
    private RedissonClient redisson;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @GetMapping({"/", "/index.html"})
    public String indexPage(Model model) {
        // 查出所有的一级分类
        List<CategoryEntity> categoryEntities = categoryService.getLevel1Categories();
        model.addAttribute("categoryEntities", categoryEntities);
        // 默认前缀 classpath:/templates/
        // 默认后缀 .html
        return "index";
    }

    @ResponseBody
    @GetMapping("index/catalog.json")
    public Map<String, List<Catalog2Vo>> getCatalogJson() {
        Map<String, List<Catalog2Vo>> catalogJson = categoryService.getCatalogJson();
        return catalogJson;
    }

    @ResponseBody
    @GetMapping("/hello")
    public String hello() {
        // 1. 获取一把锁，只要锁的名字一样，那就是同一把锁
        RLock rLock = redisson.getLock("my-lock");
        // 2. 加锁
        // rLock.lock(); // 阻塞时等待
        rLock.lock(10, TimeUnit.SECONDS);
        try {
            System.out.println("加锁成功，执行业务..." + Thread.currentThread().getName());
            Thread.sleep(10000);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            System.out.println("释放锁..." + Thread.currentThread().getName());
            rLock.unlock();
        }
        return "hello";
    }

    @ResponseBody
    @GetMapping("/write")
    public String write() {
        RReadWriteLock readWriteLock = redisson.getReadWriteLock("rw-lock");
        String s = "";
        RLock rLock = readWriteLock.writeLock();
        // 1. 该数据加写锁，读数据加读锁
        rLock.lock();
        System.out.println("写锁加锁成功..." + Thread.currentThread().getName());
        try {
            s = UUID.randomUUID().toString();
            Thread.sleep(10000);
            stringRedisTemplate.opsForValue().set("writeValue", s);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            System.out.println("写锁释放..." + Thread.currentThread().getName());
            rLock.unlock();
        }
        return s;
    }

    @ResponseBody
    @GetMapping("/read")
    public String read() {
        RReadWriteLock readWriteLock = redisson.getReadWriteLock("rw-lock");
        RLock rLock = readWriteLock.readLock();
        String s = "";
        rLock.lock();
        System.out.println("读锁加锁成功..." + Thread.currentThread().getName());
        try {
            s = stringRedisTemplate.opsForValue().get("writeValue");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            System.out.println("读锁释放..." + Thread.currentThread().getName());
            rLock.unlock();
        }
        return s;
    }

    /**
     * 车库停车，3 车位
     */
    @GetMapping("/park")
    @ResponseBody
    public String park() throws InterruptedException {
        RSemaphore rSemaphore = redisson.getSemaphore("park");
        rSemaphore.acquire(); // 获取一个信号，阻塞方法，即占一个车位
        return "ok";
    }

    @GetMapping("/go")
    @ResponseBody
    public String go() {
        RSemaphore rSemaphore = redisson.getSemaphore("park");
        rSemaphore.release(); // 释放信号
        return "ok";
    }

    @GetMapping("/lockDoor")
    @ResponseBody
    public String lockDoor() throws InterruptedException {
        RCountDownLatch rCountDownLatch = redisson.getCountDownLatch("door");
        rCountDownLatch.trySetCount(5);
        rCountDownLatch.await();
        return "放假了";
    }

    @GetMapping("/gogogo/{id}")
    @ResponseBody
    public String gogogo(@PathVariable("id") Integer id) {
        RCountDownLatch rCountDownLatch = redisson.getCountDownLatch("door");
        rCountDownLatch.countDown(); // 计数减一
        return id + " 班的人走了";
    }

}
