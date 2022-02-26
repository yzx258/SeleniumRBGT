package com.example.selenium.task;

import com.example.selenium.bet.BetYaBoUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * @program: rbgt
 * @author: 俞春旺
 * @create: 2020/06/14 09:03
 * @description： 描述：
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class TaskUtil {

    private final BetYaBoUtil betYaBoUtil;

    /**
     * 描述：发起下注
     */
    @Async("myTaskAsyncPool")
    public void getBasketballTournament2() {
        betYaBoUtil.bet();
    }
}