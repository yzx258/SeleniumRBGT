package com.example.selenium.util;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.example.selenium.entity.BetLock;
import com.example.selenium.mapper.BetLockMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author 俞春旺
 * @program: SeleniumRBGT
 * @date 2022-02-20 12:35:33
 * @description: 描述
 */
@RequiredArgsConstructor
@Component
public class CacheMapUtil {

    @Autowired
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
        if (betLock == null) return null;
        return betLock.getLockValue();
    }

    /**
     * 设定值
     *
     * @param key
     * @param var
     */
    public void putMap(String key, String var) {
        BetLock betLock = new BetLock();
        betLock.setLockKey(key);
        betLock.setLockValue(var);
        betLockMapper.insert(betLock);
    }

    /**
     * 清空锁
     *
     * @param key
     */
    public void delMap(String key) {
        betLockMapper.delete(Wrappers.lambdaQuery(BetLock.class).eq(BetLock::getLockKey, key));
    }

}
