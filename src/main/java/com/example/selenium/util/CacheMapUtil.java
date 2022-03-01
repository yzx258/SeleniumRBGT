package com.example.selenium.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.example.selenium.entity.BetLock;
import com.example.selenium.handle.BetLockHandle;

import lombok.RequiredArgsConstructor;

/**
 * @author 俞春旺
 * @program: SeleniumRBGT
 * @date 2022-02-20 12:35:33
 * @description: 描述
 */
@Component
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class CacheMapUtil {

    private final BetLockHandle betLockHandle;

    /***
     * getInfoByKey
     *
     * @param key
     * @return com.example.selenium.entity.BetLock
     * @author yucw
     * @date 2022-02-28 16:35
     */
    public BetLock getInfoByKey(String key) {
        // 获取 - 缓存信息
        BetLock betLock = betLockHandle.getInfoByKey(key);
        return betLock;
    }

    /**
     * 获取值
     *
     * @param key
     * @return
     */
    public String getMap(String key) {
        // 获取 - 缓存信息
        return betLockHandle.getMap(key);
    }

    /**
     * 设定值
     *
     * @param key
     * @param var
     */
    public String putMap(String key, String var) {
        return betLockHandle.putMap(key, var);
    }

    /**
     * 清空锁
     *
     * @param key
     */
    public void delMap(String key) {
        betLockHandle.delMap(key);
    }

    /***
     * GET - BET_URL
     * 
     * @param key
     * @return java.lang.String
     * @author yucw
     * @date 2022-02-23 13:54
     */
    public String getBetUrl(String key) {
        return betLockHandle.getBetUrl(key);
    }
}
