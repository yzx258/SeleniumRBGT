package com.example.selenium.task;

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

    @Autowired
    private TaskUtil taskService;

    @Scheduled(cron = "0/30 * * * * ?")
    private void sendYaBoBet() {
        taskService.sendYaBoBet1();
        taskService.sendYaBoBet2();
        taskService.sendYaBoBet3();
        taskService.sendYaBoBet4();

    }

    @Scheduled(cron = "0/30 * * * * ?")
    private void comparisonResults() {
        taskService.comparisonResults();
    }

    @Scheduled(cron = "0/30 * * * * ?")
    private void compatibleData() {
        taskService.compatibleData();
    }
}
