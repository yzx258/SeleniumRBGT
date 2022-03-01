package com.example.selenium.task;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.example.selenium.bet.BetYaBoUtil_3;
import com.example.selenium.handle.BetGameInfoHandle;

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
    public void sendYaBoBet() {
        System.out.println("task sendYaBoBet");
        betYaBoUtil.bet("THREAD_EXECUTION_A", 0, "BET_A");
        betYaBoUtil.bet("THREAD_EXECUTION_B", 1, "BET_B");
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