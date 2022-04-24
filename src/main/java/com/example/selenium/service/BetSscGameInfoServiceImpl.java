package com.example.selenium.service;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.selenium.entity.ssc.BetSscGameInfo;
import com.example.selenium.mapper.BetSscGameInfoMapper;

import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * File Description
 *
 * @author 俞春旺
 * @company 厦门市宜车时代
 * @date 2022-04-20 15:35
 */
@Slf4j
@Service
public class BetSscGameInfoServiceImpl extends ServiceImpl<BetSscGameInfoMapper, BetSscGameInfo>
    implements BetSscGameInfoService {

    @Override
    public void updateBetSscGameInfo(String period, Integer bsResult) {
        boolean updateResult = update(Wrappers.lambdaUpdate(BetSscGameInfo.class)
            .set(BetSscGameInfo::getResult, bsResult).eq(BetSscGameInfo::getPeriod, period));
        if (!updateResult) {
            throw new RuntimeException("更新数据失败");
        }
    }

    @Override
    public BetSscGameInfo getBetSscGameInfo(String period, Integer sscNumType) {
        log.info("==================================");
        log.info("查询购买信息：期数：{}，类型：{}", period, sscNumType);
        BetSscGameInfo betSscGameInfo = this.baseMapper.selectOne(Wrappers.lambdaQuery(BetSscGameInfo.class)
            .eq(BetSscGameInfo::getPeriod, period).eq(BetSscGameInfo::getSscNumType, sscNumType));
        log.info("查询购买信息：BetSscGameInfo：{}", JSONUtil.toJsonStr(betSscGameInfo));
        log.info("==================================");
        System.out.println();
        return betSscGameInfo;
    }
}
