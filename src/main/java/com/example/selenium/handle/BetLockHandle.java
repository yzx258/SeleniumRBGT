package com.example.selenium.handle;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.example.selenium.entity.BetLock;
import com.example.selenium.mapper.BetLockMapper;

import lombok.RequiredArgsConstructor;

/**
 * File Description
 *
 * @author 俞春旺
 * @company 厦门市宜车时代
 * @date 2022-02-28 11:24
 */
@Component
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class BetLockHandle {

    private final BetLockMapper betLockMapper;

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
        BetLock betLock = betLockMapper.selectOne(Wrappers.lambdaQuery(BetLock.class).eq(BetLock::getLockKey, key));
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
    public String putMap(String key, String var) {
        // 操作 - 查询是否存在
        BetLock betLock = betLockMapper.selectOne(Wrappers.lambdaQuery(BetLock.class).eq(BetLock::getLockKey, key));
        if (null != betLock && StringUtils.isNotEmpty(betLock.getId())) {
            betLockMapper.deleteById(betLock);
        }

        // 操作 - 插入语句
        BetLock save = new BetLock();
        save.setLockKey(key);
        save.setLockValue(var);
        betLockMapper.insert(save);
        return save.getId();
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
