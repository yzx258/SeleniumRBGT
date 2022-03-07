package com.example.selenium.task;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.example.selenium.bet.BetYaBoUtil_3;
import com.example.selenium.handle.BetGameInfoHandle;
import com.example.selenium.util.SleepUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * @program: rbgt
 * @author: 俞春旺
 * @create: 2020/06/14 09:03 @description： 描述：
 */
@Service
@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class TaskUtil {

    private final BetYaBoUtil_3 betYaBoUtil;
    private final BetGameInfoHandle betGameInfoHandle;

    /**
     * sendYaBoBet
     */
    @Async("myTaskAsyncPool")
    public void sendYaBoBet1() {
        System.out.println("TASK SEND_YA_BO_BET THREAD_EXECUTION_A");
        betYaBoUtil.bet("THREAD_EXECUTION_A", 0, "BET_A", 0);
    }

    /**
     * sendYaBoBet
     */
    @Async("myTaskAsyncPool")
    public void sendYaBoBet4() {
        System.out.println("TASK SEND_YA_BO_BET THREAD_EXECUTION_AD");
        betYaBoUtil.bet("THREAD_EXECUTION_AD", 0, "BET_AD", 1);
    }

    /**
     * sendYaBoBet
     */
    @Async("myTaskAsyncPool")
    public void sendYaBoBet2() {
        SleepUtil.sleepUtil(2000);
        System.out.println("TASK SEND_YA_BO_BET THREAD_EXECUTION_B");
        betYaBoUtil.bet("THREAD_EXECUTION_B", 1, "BET_B", 0);
    }

    /**
     * sendYaBoBet
     */
    @Async("myTaskAsyncPool")
    public void sendYaBoBet5() {
        SleepUtil.sleepUtil(2000);
        System.out.println("TASK SEND_YA_BO_BET THREAD_EXECUTION_BE");
        betYaBoUtil.bet("THREAD_EXECUTION_BE", 1, "BET_BE", 1);
    }

    /**
     * sendYaBoBet
     */
    @Async("myTaskAsyncPool")
    public void sendYaBoBet3() {
        SleepUtil.sleepUtil(4000);
        System.out.println("TASK SEND_YA_BO_BET THREAD_EXECUTION_C");
        betYaBoUtil.bet("THREAD_EXECUTION_C", 2, "BET_C", 0);
    }

    /**
     * comparisonResults
     */
    @Async("myTaskAsyncPool")
    public void comparisonResults() {
        System.out.println("task comparisonResults");
        betGameInfoHandle.comparisonResults();
    }
}