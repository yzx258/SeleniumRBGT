package com.example.selenium;

import com.example.selenium.task.TaskUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author yiautos
 */
@SpringBootApplication
public class SeleniumApplication {

    public static void main(String[] args) {
        SpringApplication.run(SeleniumApplication.class, args);
        // 异步执行下注数据
        TaskUtil taskService = new TaskUtil();
        taskService.getBasketballTournament1();
    }

}
