package com.example.selenium.task;

import cn.hutool.cache.Cache;
import cn.hutool.cache.CacheUtil;
import cn.hutool.core.date.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * @author 俞春旺
 */
@Component
@Configuration
@EnableScheduling
public class TimingTask {

    private static Cache<String,String> fifoCache = CacheUtil.newFIFOCache(10);

    @Autowired
    private TaskUtil taskService;

//    /**
//     * 每天3小时获取比赛，并入库
//     */
//    @Scheduled(cron = "0 0/1 * * * ? ")
//    private void getStartGame() {
//        System.out.println("异步线程开始");
//        taskService.getBasketballTournament();
//        System.out.println("异步线程结束");
//    }
//
    @Scheduled(cron = "0 0/1 * * * ? ")
    private void getStartGame1() {
        System.out.println("异步线程开始");
        taskService.getBasketballTournament1();
        System.out.println("异步线程结束");
    }
}
