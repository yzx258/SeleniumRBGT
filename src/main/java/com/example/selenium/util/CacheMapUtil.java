package com.example.selenium.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.example.selenium.entity.BetLock;
import com.example.selenium.mapper.BetLockMapper;

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

    private final BetLockMapper betLockMapper;

    /**
     * 获取值
     *
     * @param key
     * @return
     */
    public String getMap(String key) {
        // 获取 - 缓存信息
        BetLock betLock = betLockMapper.selectOne(Wrappers.lambdaQuery(BetLock.class).eq(BetLock::getLockKey, key));
        if (betLock == null)
            return null;
        return betLock.getLockValue();
    }

    /**
     * 设定值
     *
     * @param key
     * @param var
     */
    public void putMap(String key, String var) {
        // 操作 - 查询是否存在
        BetLock betLock = betLockMapper.selectOne(Wrappers.lambdaQuery(BetLock.class).eq(BetLock::getLockKey, key));
        if (null == betLock) {
            // 操作 - 插入语句
            betLock = new BetLock();
            betLock.setLockKey(key);
            betLock.setLockValue(var);
            betLockMapper.insert(betLock);
        } else {
            betLock.setLockValue(var);
            betLock.setLockKey(key);
            betLockMapper.updateById(betLock);
        }
    }

    /**
     * 清空锁
     *
     * @param key
     */
    public void delMap(String key) {
        BetLock betLock = betLockMapper.selectOne(Wrappers.lambdaQuery(BetLock.class).eq(BetLock::getLockKey, key));
        if (null != betLock && null != betLock.getId()) {
            betLockMapper.deleteById(betLock);
        }
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
        BetLock betLock = betLockMapper.selectOne(Wrappers.lambdaQuery(BetLock.class).eq(BetLock::getLockKey, key));
        if (null != betLock && null != betLock.getId()) {
            return betLock.getLockValue();
        }
        return "";
    }
}
